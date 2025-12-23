package dev.tagtag.module.auth.service.impl;

import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.service.JwtService;
import dev.tagtag.framework.security.service.TokenVersionService;
import dev.tagtag.module.auth.service.AuthService;
import dev.tagtag.module.auth.service.PermissionResolver;
import dev.tagtag.module.auth.service.TokenFactory;
import dev.tagtag.framework.constant.SecurityClaims;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import dev.tagtag.common.constant.GlobalConstants;

import java.util.*;

/**
 * 认证服务实现，使用 JwtProvider 生成与校验令牌
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserApi userApi;
    private final PasswordEncoder passwordEncoder;
    private final TokenVersionService tokenVersionService;
    private final PermissionResolver permissionResolver;
    private final TokenFactory tokenFactory;

    private final JwtProperties jwtProps;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录（简单校验后发放访问与刷新令牌）
     * @param username 用户名
     * @param password 密码
     * @return 令牌对象
     */
    @Override
    public TokenDTO login(String username, String password) {
        checkLoginRateLimit(username);

        String uname = normalize(username);
        String pwd = normalize(password);
        if (!org.springframework.util.StringUtils.hasText(uname) || !org.springframework.util.StringUtils.hasText(pwd)) {
            log.warn("login failed: blank credentials username='{}', ip={}, ua={}, traceId={}",
                    uname, resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "用户名或密码不能为空");
        }


        UserDTO full = loadUserOrFail(uname);
        String stored = normalize(full.getPassword());
        if (stored != null && stored.startsWith("{bcrypt}")) {
            stored = stored.substring(8);
        }
        log.info("调试信息: username={}, 存储的密码='{}'", uname, stored);
        boolean matched = passwordEncoder.matches(pwd, stored);
        log.info("调试信息: 密码匹配结果={}, 输入密码='{}'", matched, pwd);
        if (!matched) {
            log.warn("login failed: invalid credentials username='{}', ip={}, ua={}, traceId={}",
                    uname, resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "凭证无效");
        }

        List<Long> roleIds = Objects.requireNonNullElse(full.getRoleIds(), Collections.emptyList());
        Set<String> perms = permissionResolver.resolvePermissions(roleIds);

        long ver = tokenVersionService.getCurrentVersion(full.getId());
        Map<String, Object> claims = tokenFactory.buildClaims(full, roleIds, perms, ver);
        TokenDTO dto = tokenFactory.issueTokens(claims, uname, jwtProps.getAccessTtlSeconds(), jwtProps.getRefreshTtlSeconds());
        log.info("login success: uid={}, roles={}, perms={}, ip={}, ua={}, traceId={}",
                full.getId(), roleIds.size(), perms.size(), resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
        return dto;
    }


    /**
     * 刷新令牌（校验刷新令牌有效后重新生成访问与刷新令牌）
     * @param refreshToken 刷新令牌
     * @return 新令牌对象
     */
    @Override
    public TokenDTO refresh(String refreshToken) {
        checkRefreshRateLimit();
        if (!org.springframework.util.StringUtils.hasText(refreshToken) || !jwtService.validateToken(refreshToken)) {
            log.warn("refresh failed: invalid token, ip={}, ua={}, traceId={}",
                    resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "凭证无效");
        }
        String subject = jwtService.getSubject(refreshToken);
        Map<String, Object> claims = new HashMap<>(jwtService.getClaims(refreshToken));
        // 校验令牌版本是否仍然有效
        Long uid = claims.get(SecurityClaims.UID) == null ? null : toLong(claims.get(SecurityClaims.UID));
        Long tokenVer = claims.get(SecurityClaims.VER) == null ? null : toLong(claims.get(SecurityClaims.VER));
        if (uid == null || tokenVer == null || !tokenVersionService.isTokenVersionValid(uid, tokenVer)) {
            log.warn("refresh failed: token version mismatch uid={} tokenVer={}, ip={}, ua={}, traceId={}",
                    uid, tokenVer, resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "凭证无效");
        }
        claims.put(SecurityClaims.TYP, "access");
        String access = jwtService.generateToken(claims, subject, jwtProps.getAccessTtlSeconds());
        Map<String, Object> refreshClaims = new HashMap<>(claims);
        refreshClaims.put(SecurityClaims.TYP, "refresh");
        String refresh = jwtService.generateToken(refreshClaims, subject, jwtProps.getRefreshTtlSeconds());
        TokenDTO dto = new TokenDTO();
        dto.setAccessToken(access);
        dto.setRefreshToken(refresh);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(jwtProps.getAccessTtlSeconds());
        log.info("refresh success: uid={}, ip={}, ua={}, traceId={}", uid, resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
        return dto;
    }

    /**
     * 注销登录（JWT 无状态，这里直接成功）
     * @param accessToken 访问令牌
     */
    @Override
    public void logout(String accessToken) {
        // 解析令牌并提升令牌版本，使旧令牌全部失效
        if (!org.springframework.util.StringUtils.hasText(accessToken) || !jwtService.validateToken(accessToken)) {
            return;
        }
        Map<String, Object> claims = jwtService.getClaims(accessToken);
        Long uid = claims.get(SecurityClaims.UID) == null ? null : toLong(claims.get(SecurityClaims.UID));
        if (uid != null) {
            tokenVersionService.bumpVersion(uid);
            log.info("logout: bumped token version for uid={}, ip={}, ua={}, traceId={}",
                    uid, resolveClientIp(), getUserAgent(), MDC.get(GlobalConstants.TRACE_ID_MDC_KEY));
        }
    }

    /**
     * 将任意对象转换为 Long（不可转换时返回 null）
     * @param src 源对象
     * @return Long 或 null
     */
    private Long toLong(Object src) {
        if (src == null) return null;
        if (src instanceof Number n) return n.longValue();
        String s = String.valueOf(src);
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 用户注册：检查用户名唯一性，密码加密后创建用户
     * @param username 用户名
     * @param password 明文密码
     */
    @Override
    public void register(String username, String password) {
        String uname = normalize(username);
        String pwd = normalize(password);
        if (!org.springframework.util.StringUtils.hasText(uname) || !org.springframework.util.StringUtils.hasText(pwd)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "用户名或密码不能为空");
        }
        if (!isStrongPassword(pwd)) {
            throw BusinessException.of(ErrorCode.UNPROCESSABLE_ENTITY, "密码至少8位，需包含大写、小写、数字和特殊字符");
        }
        UserDTO exists = userApi.getUserByUsername(uname).getData();
        if (exists != null && exists.getId() != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "用户名已存在");
        }
        UserDTO toCreate = new UserDTO();
        toCreate.setUsername(uname);
        toCreate.setPassword(pwd);
        toCreate.setStatus(1);
        userApi.createUser(toCreate);
    }

    /**
     * 按用户名加载用户，不存在或凭证为空时抛出未认证异常
     * @param uname 用户名
     * @return 用户详情
     */
    private UserDTO loadUserOrFail(String uname) {
        UserDTO full = userApi.getUserByUsername(uname).getData();
        if (full == null || full.getPassword() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "凭证无效");
        }
        return full;
    }

    /**
     * 登录接口限流：基于用户名 + IP 的滑窗计数，超过阈值抛出 429
     * @param username 用户名
     */
    private void checkLoginRateLimit(String username) {
        try {
            String ip = resolveClientIp();
            String uname = normalize(username);
            String key = "rl:login:" + (ip == null ? "unknown" : ip) + ":" + (uname == null ? "" : uname);
            long maxPerMinute = 10L;
            Long n = stringRedisTemplate.opsForValue().increment(key);
            if (n != null && n == 1L) {
                stringRedisTemplate.expire(key, java.time.Duration.ofMinutes(1));
            }
            if (n != null && n > maxPerMinute) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
            }
        } catch (Exception ignore) {
            return;
        }
    }

    /**
     * 刷新接口限流：基于 IP 的滑窗计数，超过阈值抛出 429
     */
    private void checkRefreshRateLimit() {
        try {
            String ip = resolveClientIp();
            String key = "rl:refresh:" + (ip == null ? "unknown" : ip);
            long maxPerMinute = 30L;
            Long n = stringRedisTemplate.opsForValue().increment(key);
            if (n != null && n == 1L) {
                stringRedisTemplate.expire(key, java.time.Duration.ofMinutes(1));
            }
            if (n != null && n > maxPerMinute) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
            }
        } catch (Exception ignore) {
            return;
        }
    }

    /**
     * 解析客户端 IP（优先使用反向代理头部）
     * @return 客户端 IP
     */
    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (org.springframework.util.StringUtils.hasText(xff)) {
                int idx = xff.indexOf(',');
                return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
            }
            String rip = req.getHeader("X-Real-IP");
            if (org.springframework.util.StringUtils.hasText(rip)) return rip.trim();
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    /**
     * 读取客户端 User-Agent
     * @return UA 字符串
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String ua = req.getHeader(GlobalConstants.HEADER_USER_AGENT);
            return (ua == null ? null : ua.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验密码强度：至少8位，包含大写、小写、数字和特殊字符
     * @param raw 明文密码
     * @return 是否满足强度
     */
    private boolean isStrongPassword(String raw) {
        if (raw == null) return false;
        return raw.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$");
    }
}


package dev.tagtag.module.auth.service.impl;

import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.iam.api.RoleApi;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.util.Numbers;
import dev.tagtag.common.util.Strings;
import dev.tagtag.framework.security.JwtService;
import dev.tagtag.framework.security.TokenVersionService;
import dev.tagtag.module.auth.service.AuthService;
import dev.tagtag.module.auth.service.PermissionResolver;
import dev.tagtag.module.auth.service.TokenFactory;
import dev.tagtag.kernel.constant.SecurityMessages;
import dev.tagtag.framework.config.SecurityJwtProperties;
import dev.tagtag.kernel.constant.SecurityClaims;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

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

    private final SecurityJwtProperties jwtProps;

    /**
     * 用户登录（简单校验后发放访问与刷新令牌）
     * @param username 用户名
     * @param password 密码
     * @return 令牌对象
     */
    @Override
    public TokenDTO login(String username, String password) {

        String uname = Strings.normalize(username);
        String pwd = Strings.normalize(password);
        if (!StringUtils.hasText(uname) || !StringUtils.hasText(pwd)) {
            log.warn("login failed: blank credentials username='{}'", uname);
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "用户名或密码不能为空");
        }

        UserDTO full = loadUserOrFail(uname);
        String stored = Strings.normalize(full.getPassword());
        if (!passwordEncoder.matches(pwd, stored)) {
            log.warn("login failed: invalid credentials username='{}'", uname);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, SecurityMessages.INVALID_CREDENTIALS);
        }

        List<Long> roleIds = Objects.requireNonNullElse(full.getRoleIds(), Collections.emptyList());
        Set<String> perms = permissionResolver.resolvePermissions(roleIds);

        long ver = tokenVersionService.getCurrentVersion(full.getId());
        Map<String, Object> claims = tokenFactory.buildClaims(full, roleIds, perms, ver);
        TokenDTO dto = tokenFactory.issueTokens(claims, uname, jwtProps.getAccessTtlSeconds(), jwtProps.getRefreshTtlSeconds());
        log.info("login success: uid={}, roles={}, perms={}", full.getId(), roleIds.size(), perms.size());
        return dto;
    }

    /**
     * 刷新令牌（校验刷新令牌有效后重新生成访问与刷新令牌）
     * @param refreshToken 刷新令牌
     * @return 新令牌对象
     */
    @Override
    public TokenDTO refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !jwtService.validateToken(refreshToken)) {
            log.warn("refresh failed: invalid token");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, SecurityMessages.INVALID_CREDENTIALS);
        }
        String subject = jwtService.getSubject(refreshToken);
        Map<String, Object> claims = new HashMap<>(jwtService.getClaims(refreshToken));
        // 校验令牌版本是否仍然有效
        Long uid = claims.get(SecurityClaims.UID) == null ? null : Numbers.toLong(claims.get(SecurityClaims.UID));
        Long tokenVer = claims.get(SecurityClaims.VER) == null ? null : Numbers.toLong(claims.get(SecurityClaims.VER));
        if (uid == null || tokenVer == null || !tokenVersionService.isTokenVersionValid(uid, tokenVer)) {
            log.warn("refresh failed: token version mismatch uid={} tokenVer={}", uid, tokenVer);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, SecurityMessages.INVALID_CREDENTIALS);
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
        return dto;
    }

    /**
     * 注销登录（JWT 无状态，这里直接成功）
     * @param accessToken 访问令牌
     */
    @Override
    public void logout(String accessToken) {
        // 解析令牌并提升令牌版本，使旧令牌全部失效
        if (!StringUtils.hasText(accessToken) || !jwtService.validateToken(accessToken)) {
            return;
        }
        Map<String, Object> claims = jwtService.getClaims(accessToken);
        Long uid = claims.get(SecurityClaims.UID) == null ? null : Numbers.toLong(claims.get(SecurityClaims.UID));
        if (uid != null) {
            tokenVersionService.bumpVersion(uid);
            log.info("logout: bumped token version for uid={}", uid);
        }
    }  

    /**
     * 按用户名加载用户，不存在或凭证为空时抛出未认证异常
     * @param uname 用户名
     * @return 用户详情
     */
    private UserDTO loadUserOrFail(String uname) {
        UserDTO full = userApi.getUserByUsername(uname).getData();
        if (full == null || full.getPassword() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, SecurityMessages.INVALID_CREDENTIALS);
        }
        return full;
    }
}

package dev.tagtag.module.auth.service.impl;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.iam.api.RoleApi;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.framework.security.JwtService;
import dev.tagtag.module.auth.service.AuthService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务实现，使用 JwtProvider 生成与校验令牌
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserApi userApi;
    private final RoleApi roleApi;

    private static final long ACCESS_TTL_SECONDS = 3600;      // 1小时
    private static final long REFRESH_TTL_SECONDS = 7 * 24 * 3600; // 7天

    /**
     * 用户登录（简单校验后发放访问与刷新令牌）
     * @param username 用户名
     * @param password 密码
     * @return 令牌对象
     */
    @Override
    public TokenDTO login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        // 通过 IAM 契约查询用户与权限
        UserQueryDTO filter = UserQueryDTO.builder().username(username).build();
        PageQuery pq = new PageQuery();
        pq.setPageNo(1);
        pq.setPageSize(1);
        PageResult<UserDTO> pr = userApi.listUsers(pq, filter).getData();
        List<UserDTO> users = pr == null ? java.util.Collections.emptyList() : pr.getList();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }
        UserDTO u = users.get(0);
        List<Long> roleIds = u.getRoleIds() == null ? java.util.Collections.emptyList() : u.getRoleIds();
        java.util.Set<String> perms = new java.util.HashSet<>();
        for (Long rid : roleIds) {
            List<MenuDTO> list = roleApi.listMenusByRole(rid).getData();
            if (list != null) {
                for (MenuDTO m : list) if (m != null && m.getMenuCode() != null) perms.add(m.getMenuCode());
            }
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", u.getId());
        claims.put("uname", u.getUsername());
        claims.put("roles", roleIds);
        claims.put("perms", perms);
        claims.put("typ", "access");
        String access = jwtService.generateToken(claims, username, ACCESS_TTL_SECONDS);
        claims.put("typ", "refresh");
        String refresh = jwtService.generateToken(claims, username, REFRESH_TTL_SECONDS);
        TokenDTO dto = new TokenDTO();
        dto.setAccessToken(access);
        dto.setRefreshToken(refresh);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(ACCESS_TTL_SECONDS);
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
            throw new IllegalArgumentException("刷新令牌无效");
        }
        String subject = jwtService.getSubject(refreshToken);
        Map<String, Object> claims = new HashMap<>(jwtService.getClaims(refreshToken));
        claims.put("typ", "access");
        String access = jwtService.generateToken(claims, subject, ACCESS_TTL_SECONDS);
        claims.put("typ", "refresh");
        String refresh = jwtService.generateToken(claims, subject, REFRESH_TTL_SECONDS);
        TokenDTO dto = new TokenDTO();
        dto.setAccessToken(access);
        dto.setRefreshToken(refresh);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(ACCESS_TTL_SECONDS);
        return dto;
    }

    /**
     * 注销登录（JWT 无状态，这里直接成功）
     * @param accessToken 访问令牌
     */
    @Override
    public void logout(String accessToken) {
        // 无状态，不做持久化处理
    }
}

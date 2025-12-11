package dev.tagtag.module.auth.service;

import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.framework.security.JwtService;
import dev.tagtag.kernel.constant.SecurityClaims;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 令牌工厂：构造 Claims 并发放访问与刷新令牌
 */
@Service
public class TokenFactory {

    private final JwtService jwtService;

    /**
     * 构造函数：注入 JWT 服务
     * @param jwtService JWT 服务
     */
    public TokenFactory(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * 构造 JWT Claims 映射
     * @param full 用户详情
     * @param roleIds 角色ID列表
     * @param perms 权限集合
     * @param ver 令牌版本
     * @return Claims 映射
     */
    public Map<String, Object> buildClaims(UserDTO full, List<Long> roleIds, Set<String> perms, long ver) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityClaims.UID, full.getId());
        claims.put(SecurityClaims.UNAME, full.getUsername());
        claims.put(SecurityClaims.ROLES, roleIds);
        claims.put(SecurityClaims.PERMS, perms);
        claims.put(SecurityClaims.VER, ver);
        claims.put(SecurityClaims.IS_ADMIN, full.getIsAdmin());
        return claims;
    }

    /**
     * 根据 Claims 生成访问与刷新令牌并封装为 TokenDTO
     * @param claims 载荷
     * @param subject 主体（用户名）
     * @param accessTtlSeconds 访问令牌有效期（秒）
     * @param refreshTtlSeconds 刷新令牌有效期（秒）
     * @return 令牌对象
     */
    public TokenDTO issueTokens(Map<String, Object> claims, String subject, long accessTtlSeconds, long refreshTtlSeconds) {
        Map<String, Object> accessClaims = new HashMap<>(claims);
        accessClaims.put(SecurityClaims.TYP, "access");
        String access = jwtService.generateToken(accessClaims, subject, accessTtlSeconds);

        Map<String, Object> refreshClaims = new HashMap<>(claims);
        refreshClaims.put(SecurityClaims.TYP, "refresh");
        String refresh = jwtService.generateToken(refreshClaims, subject, refreshTtlSeconds);

        TokenDTO dto = new TokenDTO();
        dto.setAccessToken(access);
        dto.setRefreshToken(refresh);
        dto.setTokenType("Bearer");
        dto.setExpiresIn(accessTtlSeconds);
        return dto;
    }
}
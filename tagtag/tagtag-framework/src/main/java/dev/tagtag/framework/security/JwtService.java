package dev.tagtag.framework.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 基于 Nimbus 的 JWT 服务，负责令牌的生成与校验
 */
@Component
public class JwtService {

    @Value("${jwt.secret:tagtag-default-secret}")
    private String secret;

    /**
     * 生成 HS256 签名的 JWT
     * @param claims 业务声明
     * @param subject 主体
     * @param ttlSeconds 有效期（秒）
     * @return JWT 字符串
     */
    public String generateToken(Map<String, Object> claims, String subject, long ttlSeconds) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttlSeconds);
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .notBeforeTime(Date.from(now))
                    .jwtID(UUID.randomUUID().toString());
            if (claims != null) {
                for (Map.Entry<String, Object> e : claims.entrySet()) {
                    builder.claim(e.getKey(), e.getValue());
                }
            }
            JWTClaimsSet claimSet = builder.build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
            jwt.sign(new MACSigner(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 生成失败", e);
        }
    }

    /**
     * 校验 JWT 签名与过期时间
     * @param token JWT 字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean sigOk = jwt.verify(new MACVerifier(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            if (!sigOk) return false;
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            return exp != null && Instant.now().isBefore(exp.toInstant());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 提取 subject
     * @param token JWT 字符串
     * @return 主体
     */
    public String getSubject(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 提取 claims（不含签名部分）
     * @param token JWT 字符串
     * @return 负载映射
     */
    public Map<String, Object> getClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            return java.util.Collections.emptyMap();
        }
    }
}
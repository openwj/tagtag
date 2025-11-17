package dev.tagtag.framework.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.tagtag.framework.config.JwtProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 基于 Nimbus 的 JWT 服务，负责令牌的生成与校验
 */
@Component
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * 构造函数：注入 JWT 配置
     * @param jwtProperties JWT 配置
     */
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成签名的 JWT（优先使用 RS256；若无 RSA 私钥则回退 HS256）
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
            if (jwtProperties.getIssuer() != null && !jwtProperties.getIssuer().isBlank()) {
                builder.issuer(jwtProperties.getIssuer());
            }
            if (jwtProperties.getAudience() != null && !jwtProperties.getAudience().isBlank()) {
                builder.audience(java.util.List.of(jwtProperties.getAudience()));
            }
            if (claims != null) {
                for (Map.Entry<String, Object> e : claims.entrySet()) {
                    builder.claim(e.getKey(), e.getValue());
                }
            }
            JWTClaimsSet claimSet = builder.build();
            SignedJWT jwt;
            String privPem = jwtProperties.getPrivateKeyPem();
            if (privPem != null && !privPem.isBlank()) {
                jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimSet);
                jwt.sign(new RSASSASigner(loadPrivateKey(privPem)));
            } else {
                jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimSet);
                jwt.sign(new MACSigner(jwtProperties.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            }
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT 生成失败", e);
        }
    }

    /**
     * 校验 JWT 签名、过期与可选的发行者/受众
     * @param token JWT 字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            boolean sigOk;
            String pubPem = jwtProperties.getPublicKeyPem();
            if (pubPem != null && !pubPem.isBlank()) {
                sigOk = jwt.verify(new RSASSAVerifier((java.security.interfaces.RSAPublicKey) loadPublicKey(pubPem)));
            } else {
                sigOk = jwt.verify(new MACVerifier(jwtProperties.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            }
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

    /**
     * 解析 PKCS#8 私钥（PEM）
     * @param pem 私钥 PEM 字符串
     * @return 私钥
     */
    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String normalized = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 解析 X.509 公钥（PEM）
     * @param pem 公钥 PEM 字符串
     * @return 公钥
     */
    private PublicKey loadPublicKey(String pem) throws Exception {
        String normalized = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
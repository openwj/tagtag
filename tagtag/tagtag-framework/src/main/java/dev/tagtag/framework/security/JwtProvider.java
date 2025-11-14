package dev.tagtag.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 生成与验证工具
 */
@RequiredArgsConstructor
public class JwtProvider {

    private final ObjectMapper objectMapper;
    /**
     * -- SETTER --
     *  设置签名密钥
     */
    @Setter
    private String secret = "tagtag-default-secret";

    /**
     * 生成 HS256 签名的 JWT
     * @param claims 业务声明
     * @param subject 主体
     * @param ttlSeconds 有效期（秒）
     * @return JWT 字符串
     */
    public String generateToken(Map<String, Object> claims, String subject, long ttlSeconds) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            Map<String, Object> payload = new HashMap<>();
            if (claims != null) payload.putAll(claims);
            payload.put("sub", subject);
            long exp = Instant.now().getEpochSecond() + ttlSeconds;
            payload.put("exp", exp);
            String h = base64Url(objectMapper.writeValueAsBytes(header));
            String p = base64Url(objectMapper.writeValueAsBytes(payload));
            String unsigned = h + "." + p;
            String sig = sign(unsigned);
            return unsigned + "." + sig;
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
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String unsigned = parts[0] + "." + parts[1];
            if (!sign(unsigned).equals(parts[2])) return false;
            byte[] payloadBytes = base64UrlDecode(parts[1]);
            Map<?,?> payload = objectMapper.readValue(payloadBytes, Map.class);
            Object expObj = payload.get("exp");
            long now = Instant.now().getEpochSecond();
            long exp = expObj instanceof Number ? ((Number) expObj).longValue() : Long.parseLong(String.valueOf(expObj));
            return now <= exp;
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
            String[] parts = token.split("\\.");
            byte[] payloadBytes = base64UrlDecode(parts[1]);
            Map<?,?> payload = objectMapper.readValue(payloadBytes, Map.class);
            Object sub = payload.get("sub");
            return sub == null ? null : String.valueOf(sub);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 提取 claims（不含签名部分），已校验格式但不校验过期
     * @param token JWT 字符串
     * @return 负载映射
     */
    public Map<String, Object> getClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            byte[] payloadBytes = base64UrlDecode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(payloadBytes, Map.class);
            return payload;
        } catch (Exception e) {
            return java.util.Collections.emptyMap();
        }
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return base64Url(sig);
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private static byte[] base64UrlDecode(String text) {
        return Base64.getUrlDecoder().decode(text);
    }
}

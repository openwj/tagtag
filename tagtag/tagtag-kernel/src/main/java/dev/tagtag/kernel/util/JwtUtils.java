package dev.tagtag.kernel.util;

import dev.tagtag.common.util.JsonUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    /** 生成 HS256 签名的 JWT 字符串 */
    public static String generateToken(Map<String, Object> claims, String subject, long ttlSeconds, String secret) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            Map<String, Object> payload = new HashMap<>();
            if (claims != null) payload.putAll(claims);
            payload.put("sub", subject);
            payload.put("exp", Instant.now().getEpochSecond() + ttlSeconds);
            String h = base64Url(JsonUtils.toJson(header).getBytes(StandardCharsets.UTF_8));
            String p = base64Url(JsonUtils.toJson(payload).getBytes(StandardCharsets.UTF_8));
            String unsigned = h + "." + p;
            String sig = sign(unsigned, secret);
            return unsigned + "." + sig;
        } catch (Exception e) {
            throw new RuntimeException("JWT 生成失败", e);
        }
    }

    /** 校验 JWT 签名与过期时间 */
    public static boolean validate(String token, String secret) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String unsigned = parts[0] + "." + parts[1];
            if (!sign(unsigned, secret).equals(parts[2])) return false;
            Map<?,?> payload = JsonUtils.fromJsonBytes(base64UrlDecode(parts[1]), Map.class);
            Object expObj = payload.get("exp");
            long now = Instant.now().getEpochSecond();
            long exp = expObj instanceof Number ? ((Number) expObj).longValue() : Long.parseLong(String.valueOf(expObj));
            return now <= exp;
        } catch (Exception e) {
            return false;
        }
    }

    /** 读取 JWT 的 subject */
    public static String getSubject(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            Map<?,?> payload = JsonUtils.fromJsonBytes(base64UrlDecode(parts[1]), Map.class);
            Object sub = payload.get("sub");
            return sub == null ? null : String.valueOf(sub);
        } catch (Exception e) {
            return null;
        }
    }

    /** 读取 JWT 的某个自定义声明 */
    public static Object getClaim(String token, String name) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            Map<?,?> payload = JsonUtils.fromJsonBytes(base64UrlDecode(parts[1]), Map.class);
            return payload.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }

    private static String sign(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return base64Url(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}

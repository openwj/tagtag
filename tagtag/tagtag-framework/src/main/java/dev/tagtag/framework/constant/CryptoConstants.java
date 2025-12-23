package dev.tagtag.framework.constant;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class CryptoConstants {

    private CryptoConstants() {}

    public static final String RSA_ALGORITHM = "RSA";
    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    public static final String HMAC_SHA512_ALGORITHM = "HmacSHA512";

    public static final String RSA_PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
    public static final String RSA_PUBLIC_KEY_SUFFIX = "-----END PUBLIC KEY-----";
    public static final String RSA_PRIVATE_KEY_PREFIX = "-----BEGIN PRIVATE KEY-----";
    public static final String RSA_PRIVATE_KEY_SUFFIX = "-----END PRIVATE KEY-----";

    public static X509EncodedKeySpec parseRSAPublicKeyPem(String pem) {
        String normalized = pem.replace(RSA_PUBLIC_KEY_PREFIX, "")
                .replace(RSA_PUBLIC_KEY_SUFFIX, "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        return new X509EncodedKeySpec(keyBytes);
    }

    public static PKCS8EncodedKeySpec parseRSAPrivateKeyPem(String pem) {
        String normalized = pem.replace(RSA_PRIVATE_KEY_PREFIX, "")
                .replace(RSA_PRIVATE_KEY_SUFFIX, "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        return new PKCS8EncodedKeySpec(keyBytes);
    }

    public static KeyFactory getRSAKeyFactory() {
        try {
            return KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create RSA KeyFactory", e);
        }
    }
}

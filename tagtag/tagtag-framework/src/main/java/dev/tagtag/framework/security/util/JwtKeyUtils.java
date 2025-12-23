package dev.tagtag.framework.security.util;

import dev.tagtag.framework.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public final class JwtKeyUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
    private static final String RSA_PUBLIC_KEY_SUFFIX = "-----END PUBLIC KEY-----";
    private static final String RSA_PRIVATE_KEY_PREFIX = "-----BEGIN PRIVATE KEY-----";
    private static final String RSA_PRIVATE_KEY_SUFFIX = "-----END PRIVATE KEY-----";

    private JwtKeyUtils() {}

    public static PrivateKey loadPrivateKey(String pem) {
        try {
            var spec = parseRSAPrivateKeyPem(pem);
            return getRSAKeyFactory().generatePrivate(spec);
        } catch (Exception e) {
            log.error("Failed to load RSA private key", e);
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    public static PublicKey loadPublicKey(String pem) {
        try {
            var spec = parseRSAPublicKeyPem(pem);
            return getRSAKeyFactory().generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to load RSA public key", e);
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    public static boolean hasRSAPrivateKey(JwtProperties properties) {
        return properties.getPrivateKeyPem() != null && !properties.getPrivateKeyPem().isBlank();
    }

    public static boolean hasRSAPublicKey(JwtProperties properties) {
        return properties.getPublicKeyPem() != null && !properties.getPublicKeyPem().isBlank();
    }

    private static X509EncodedKeySpec parseRSAPublicKeyPem(String pem) {
        String normalized = pem.replace(RSA_PUBLIC_KEY_PREFIX, "")
                .replace(RSA_PUBLIC_KEY_SUFFIX, "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        return new X509EncodedKeySpec(keyBytes);
    }

    private static PKCS8EncodedKeySpec parseRSAPrivateKeyPem(String pem) {
        String normalized = pem.replace(RSA_PRIVATE_KEY_PREFIX, "")
                .replace(RSA_PRIVATE_KEY_SUFFIX, "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(normalized);
        return new PKCS8EncodedKeySpec(keyBytes);
    }

    private static KeyFactory getRSAKeyFactory() {
        try {
            return KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create RSA KeyFactory", e);
        }
    }
}

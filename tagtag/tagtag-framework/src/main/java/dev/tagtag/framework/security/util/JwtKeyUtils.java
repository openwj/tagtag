package dev.tagtag.framework.security.util;

import dev.tagtag.framework.constant.CryptoConstants;
import dev.tagtag.framework.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
public final class JwtKeyUtils {

    private JwtKeyUtils() {}

    public static PrivateKey loadPrivateKey(String pem) {
        try {
            var spec = CryptoConstants.parseRSAPrivateKeyPem(pem);
            return CryptoConstants.getRSAKeyFactory().generatePrivate(spec);
        } catch (Exception e) {
            log.error("Failed to load RSA private key", e);
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    public static PublicKey loadPublicKey(String pem) {
        try {
            var spec = CryptoConstants.parseRSAPublicKeyPem(pem);
            return CryptoConstants.getRSAKeyFactory().generatePublic(spec);
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
}

package dev.tagtag.framework.security.strategy.impl;

import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.strategy.JwtDecoderStrategy;
import dev.tagtag.framework.security.util.JwtKeyUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;

public class RsaJwtDecoderStrategy implements JwtDecoderStrategy {

    private final JwtProperties jwtProperties;

    public RsaJwtDecoderStrategy(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public JwtDecoder createDecoder() {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) JwtKeyUtils.loadPublicKey(jwtProperties.getPublicKeyPem());
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}

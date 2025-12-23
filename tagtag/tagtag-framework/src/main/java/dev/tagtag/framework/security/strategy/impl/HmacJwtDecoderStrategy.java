package dev.tagtag.framework.security.strategy.impl;

import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.strategy.JwtDecoderStrategy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import java.nio.charset.StandardCharsets;

public class HmacJwtDecoderStrategy implements JwtDecoderStrategy {

    private final JwtProperties jwtProperties;

    public HmacJwtDecoderStrategy(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public JwtDecoder createDecoder() {
        return org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(
                        jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                )
        ).macAlgorithm(MacAlgorithm.HS256).build();
    }
}

package dev.tagtag.framework.security.strategy.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import dev.tagtag.framework.security.strategy.JwtSignerStrategy;
import dev.tagtag.framework.config.JwtProperties;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HmacJwtSignerStrategy implements JwtSignerStrategy {

    private final JwtProperties jwtProperties;

    @Override
    public JWSHeader createHeader() {
        return new JWSHeader(JWSAlgorithm.HS256);
    }

    @Override
    public void sign(SignedJWT jwt) throws Exception {
        jwt.sign(new MACSigner(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public boolean verify(SignedJWT jwt) throws Exception {
        return jwt.verify(new MACVerifier(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)));
    }
}

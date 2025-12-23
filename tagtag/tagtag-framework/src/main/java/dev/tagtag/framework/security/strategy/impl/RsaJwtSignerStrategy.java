package dev.tagtag.framework.security.strategy.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import dev.tagtag.framework.security.strategy.JwtSignerStrategy;
import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.util.JwtKeyUtils;
import lombok.RequiredArgsConstructor;

import java.security.interfaces.RSAPublicKey;

@RequiredArgsConstructor
public class RsaJwtSignerStrategy implements JwtSignerStrategy {

    private final JwtProperties jwtProperties;

    @Override
    public JWSHeader createHeader() {
        return new JWSHeader(JWSAlgorithm.RS256);
    }

    @Override
    public void sign(SignedJWT jwt) throws Exception {
        jwt.sign(new RSASSASigner(JwtKeyUtils.loadPrivateKey(jwtProperties.getPrivateKeyPem())));
    }

    @Override
    public boolean verify(SignedJWT jwt) throws Exception {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) JwtKeyUtils.loadPublicKey(jwtProperties.getPublicKeyPem());
        return jwt.verify(new RSASSAVerifier(rsaPublicKey));
    }
}

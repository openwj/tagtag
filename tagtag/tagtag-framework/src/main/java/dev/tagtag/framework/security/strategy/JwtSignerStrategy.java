package dev.tagtag.framework.security.strategy;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.SignedJWT;

public interface JwtSignerStrategy {

    JWSHeader createHeader();

    void sign(SignedJWT jwt) throws Exception;

    boolean verify(SignedJWT jwt) throws Exception;
}

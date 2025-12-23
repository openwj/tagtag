package dev.tagtag.framework.security.strategy;

import org.springframework.security.oauth2.jwt.JwtDecoder;

public interface JwtDecoderStrategy {
    JwtDecoder createDecoder();
}

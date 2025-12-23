package dev.tagtag.framework.security.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.strategy.JwtSignerStrategy;
import dev.tagtag.framework.security.strategy.impl.HmacJwtSignerStrategy;
import dev.tagtag.framework.security.strategy.impl.RsaJwtSignerStrategy;
import dev.tagtag.framework.security.util.JwtKeyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final JwtSignerStrategy signerStrategy;

    public String generateToken(Map<String, Object> claims, String subject, long ttlSeconds) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttlSeconds);

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .notBeforeTime(Date.from(now))
                    .jwtID(UUID.randomUUID().toString());

            if (hasIssuer()) {
                builder.issuer(jwtProperties.getIssuer());
            }
            if (hasAudience()) {
                builder.audience(List.of(jwtProperties.getAudience()));
            }
            if (claims != null) {
                claims.forEach(builder::claim);
            }

            JWTClaimsSet claimSet = builder.build();
            SignedJWT jwt = new SignedJWT(signerStrategy.createHeader(), claimSet);
            signerStrategy.sign(jwt);

            return jwt.serialize();
        } catch (Exception e) {
            log.error("Failed to generate JWT token", e);
            throw new IllegalStateException("Failed to generate JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            if (!signerStrategy.verify(jwt)) {
                return false;
            }

            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            return exp != null && Instant.now().isBefore(exp.toInstant());
        } catch (Exception e) {
            log.debug("Token validation failed", e);
            return false;
        }
    }

    public String getSubject(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            log.debug("Failed to extract subject from token", e);
            return null;
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            log.debug("Failed to extract claims from token", e);
            return Collections.emptyMap();
        }
    }

    private boolean hasIssuer() {
        return jwtProperties.getIssuer() != null && !jwtProperties.getIssuer().isBlank();
    }

    private boolean hasAudience() {
        return jwtProperties.getAudience() != null && !jwtProperties.getAudience().isBlank();
    }
}

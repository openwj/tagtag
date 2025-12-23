package dev.tagtag.framework.security.config;

import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.strategy.JwtSignerStrategy;
import dev.tagtag.framework.security.strategy.impl.HmacJwtSignerStrategy;
import dev.tagtag.framework.security.strategy.impl.RsaJwtSignerStrategy;
import dev.tagtag.framework.security.util.JwtKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtSignerConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtSignerStrategy jwtSignerStrategy() {
        if (JwtKeyUtils.hasRSAPrivateKey(jwtProperties)) {
            return new RsaJwtSignerStrategy(jwtProperties);
        }
        return new HmacJwtSignerStrategy(jwtProperties);
    }
}

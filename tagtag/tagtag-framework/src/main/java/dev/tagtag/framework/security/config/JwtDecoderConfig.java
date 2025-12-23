package dev.tagtag.framework.security.config;

import dev.tagtag.framework.config.JwtProperties;
import dev.tagtag.framework.security.strategy.JwtDecoderStrategy;
import dev.tagtag.framework.security.strategy.impl.HmacJwtDecoderStrategy;
import dev.tagtag.framework.security.strategy.impl.RsaJwtDecoderStrategy;
import dev.tagtag.framework.security.util.JwtKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@RequiredArgsConstructor
public class JwtDecoderConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtDecoder jwtDecoder() {
        JwtDecoderStrategy strategy = createDecoderStrategy();
        return strategy.createDecoder();
    }

    @Bean
    public JwtDecoderStrategy jwtDecoderStrategy() {
        return createDecoderStrategy();
    }

    private JwtDecoderStrategy createDecoderStrategy() {
        if (JwtKeyUtils.hasRSAPublicKey(jwtProperties)) {
            return new RsaJwtDecoderStrategy(jwtProperties);
        }
        return new HmacJwtDecoderStrategy(jwtProperties);
    }
}

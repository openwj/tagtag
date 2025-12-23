package dev.tagtag.framework.web;

import dev.tagtag.framework.constant.CorsConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern(CorsConstants.ALLOWED_ORIGIN_PATTERN);
        config.addAllowedMethod(CorsConstants.ALLOWED_METHOD);
        config.addAllowedHeader(CorsConstants.ALLOWED_HEADER);
        config.setAllowCredentials(CorsConstants.ALLOW_CREDENTIALS);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(CorsConstants.PATH_PATTERN, config);
        return new CorsFilter(source);
    }
}

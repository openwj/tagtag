package dev.tagtag.framework.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private static final String ALLOWED_ORIGIN_PATTERN = "*";
    private static final String ALLOWED_METHOD = "*";
    private static final String ALLOWED_HEADER = "*";
    private static final String PATH_PATTERN = "/**";
    private static final boolean ALLOW_CREDENTIALS = false;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern(ALLOWED_ORIGIN_PATTERN);
        config.addAllowedMethod(ALLOWED_METHOD);
        config.addAllowedHeader(ALLOWED_HEADER);
        config.setAllowCredentials(ALLOW_CREDENTIALS);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(PATH_PATTERN, config);
        return new CorsFilter(source);
    }
}

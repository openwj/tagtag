package dev.tagtag.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "TagTag Starter API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "基于 Spring Boot 3.x 的企业级后台管理系统 API";
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String HEADER_NAME = "Authorization";
    private static final String AUTH_SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .version(API_VERSION)
                        .description(API_DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(HEADER_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(AUTH_SCHEME)
                                .bearerFormat(BEARER_FORMAT)
                                .in(SecurityScheme.In.HEADER)));
    }
}

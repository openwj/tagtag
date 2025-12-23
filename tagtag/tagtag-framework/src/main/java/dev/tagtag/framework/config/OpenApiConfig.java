package dev.tagtag.framework.config;

import dev.tagtag.framework.constant.OpenApiConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(OpenApiConstants.API_TITLE)
                        .version(OpenApiConstants.API_VERSION)
                        .description(OpenApiConstants.API_DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList(OpenApiConstants.SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(OpenApiConstants.SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(OpenApiConstants.HEADER_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(OpenApiConstants.AUTH_SCHEME)
                                .bearerFormat(OpenApiConstants.BEARER_FORMAT)
                                .in(SecurityScheme.In.HEADER)));
    }
}

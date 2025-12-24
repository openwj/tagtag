package dev.tagtag.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    /**
     * API 基本信息配置
     */
    private static final String API_TITLE = "TagTag 企业级后台管理系统 API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "基于 Spring Boot 3.x 开发的企业级后台管理系统，提供完整的用户管理、权限控制、数据统计等功能模块。\n" +
            "\n主要功能特性：\n" +
            "- 基于 JWT 的身份认证机制\n" +
            "- 细粒度的权限控制体系\n" +
            "- RESTful API 设计规范\n" +
            "- 完善的数据校验机制\n" +
            "- 统一的异常处理\n" +
            "- 详细的日志记录\n";

    /**
     * 联系人信息配置
     */
    private static final String CONTACT_NAME = "TagTag 开发团队";
    private static final String CONTACT_EMAIL = "dev@tagtag.cn";
    private static final String CONTACT_URL = "https://www.tagtag.cn";

    /**
     * 许可证信息配置
     */
    private static final String LICENSE_NAME = "Apache License 2.0";
    private static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";

    /**
     * 服务器配置
     */
    private static final String LOCAL_SERVER_URL = "http://localhost:8080";
    private static final String LOCAL_SERVER_DESCRIPTION = "本地开发环境";
    private static final String PROD_SERVER_URL = "https://api.tagtag.cn";
    private static final String PROD_SERVER_DESCRIPTION = "生产环境";

    /**
     * 安全认证配置
     */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";
    private static final String HEADER_NAME = "Authorization";
    private static final String AUTH_SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";
    private static final String SECURITY_DESCRIPTION = "JWT 认证令牌，格式：Bearer {token}";

    /**
     * 配置 OpenAPI 文档信息
     *
     * @return 配置完成的 OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 联系人信息
        Contact contact = new Contact()
                .name(CONTACT_NAME)
                .email(CONTACT_EMAIL)
                .url(CONTACT_URL);

        // 许可证信息
        License license = new License()
                .name(LICENSE_NAME)
                .url(LICENSE_URL);

        // API 基本信息
        Info info = new Info()
                .title(API_TITLE)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .contact(contact)
                .license(license);

        // 服务器配置
        Server localServer = new Server()
                .url(LOCAL_SERVER_URL)
                .description(LOCAL_SERVER_DESCRIPTION);

        Server prodServer = new Server()
                .url(PROD_SERVER_URL)
                .description(PROD_SERVER_DESCRIPTION);

        // 安全认证方案
        SecurityScheme securityScheme = new SecurityScheme()
                .name(HEADER_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(AUTH_SCHEME)
                .bearerFormat(BEARER_FORMAT)
                .in(SecurityScheme.In.HEADER)
                .description(SECURITY_DESCRIPTION);

        return new OpenAPI()
                // 设置 API 基本信息
                .info(info)
                // 添加服务器配置
                .servers(List.of(localServer, prodServer))
                // 添加安全认证要求
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                // 配置组件（安全方案等）
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
    }
}

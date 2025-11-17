package dev.tagtag.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * JWT 通用配置（secret）
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secret = "tagtag-default-secret";
    private String privateKeyPem;
    private String publicKeyPem;
    private String issuer;
    private String audience;
}
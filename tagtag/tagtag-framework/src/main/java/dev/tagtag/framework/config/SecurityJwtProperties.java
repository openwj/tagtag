package dev.tagtag.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * 安全相关 JWT 配置（TTL）
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class SecurityJwtProperties {

    private long accessTtlSeconds = 3600;
    private long refreshTtlSeconds = 604800;
}
package dev.tagtag.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全相关 JWT 配置（TTL）
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityJwtProperties {

    private long accessTtlSeconds = 3600;
    private long refreshTtlSeconds = 604800;

    /**
     * 获取访问令牌 TTL（秒）
     * @return TTL 秒
     */
    public long getAccessTtlSeconds() { return accessTtlSeconds; }

    /**
     * 设置访问令牌 TTL（秒）
     * @param accessTtlSeconds TTL 秒
     */
    public void setAccessTtlSeconds(long accessTtlSeconds) { this.accessTtlSeconds = accessTtlSeconds; }

    /**
     * 获取刷新令牌 TTL（秒）
     * @return TTL 秒
     */
    public long getRefreshTtlSeconds() { return refreshTtlSeconds; }

    /**
     * 设置刷新令牌 TTL（秒）
     * @param refreshTtlSeconds TTL 秒
     */
    public void setRefreshTtlSeconds(long refreshTtlSeconds) { this.refreshTtlSeconds = refreshTtlSeconds; }
}
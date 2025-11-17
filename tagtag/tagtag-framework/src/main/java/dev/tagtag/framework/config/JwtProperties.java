package dev.tagtag.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 通用配置（secret）
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "tagtag-default-secret";

    /**
     * 获取 JWT 密钥
     * @return 密钥
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 设置 JWT 密钥
     * @param secret 密钥
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }
}
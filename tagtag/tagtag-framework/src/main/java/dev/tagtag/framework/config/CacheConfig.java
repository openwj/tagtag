package dev.tagtag.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {

    private Map<String, Duration> ttl = new HashMap<>();
    private Duration defaultTtl = Duration.ofMinutes(5);

    /**
     * 注册 RedisCacheManager，按缓存名设置独立 TTL，并统一序列化策略
     * @param connectionFactory Redis 连接工厂
     * @param objectMapper 全局 ObjectMapper
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .disableCachingNullValues()
                .entryTtl(defaultTtl);

        Map<String, RedisCacheConfiguration> initialConfigs = new HashMap<>();
        if (ttl != null) {
            ttl.forEach((name, d) -> {
                if (name != null && !name.isBlank() && d != null && !d.isNegative() && !d.isZero()) {
                    initialConfigs.put(name, defaultConfig.entryTtl(d));
                }
            });
        }

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(initialConfigs)
                .build();
    }

    public Map<String, Duration> getTtl() { return ttl; }
    public void setTtl(Map<String, Duration> ttl) { this.ttl = ttl; }
    public Duration getDefaultTtl() { return defaultTtl; }
    public void setDefaultTtl(Duration defaultTtl) { this.defaultTtl = defaultTtl; }
}
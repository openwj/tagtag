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
import lombok.Data;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache")
@Data
public class CacheConfig {

    private Map<String, Duration> ttl = new HashMap<>();
    private Duration defaultTtl = Duration.ofMinutes(5);

    /**
     * 注册 RedisCacheManager（启用值类型信息，避免集合类型反序列化丢失）
     * @param connectionFactory Redis 连接工厂
     * @param objectMapper 全局 ObjectMapper（不参与缓存值序列化）
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

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

}
package dev.tagtag.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tagtag.kernel.constant.CacheConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache")
@Data
public class CacheConfig {

    private Map<String, Duration> ttl = new HashMap<>();
    private Duration defaultTtl = CacheConstants.DEFAULT_TTL;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .disableCachingNullValues()
                .entryTtl(defaultTtl)
                .computePrefixWith(cacheName -> CacheConstants.PREFIX + ":" + cacheName + ":");

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

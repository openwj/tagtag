package dev.tagtag.framework.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存基础配置（使用并发 Map 作为本地缓存实现）
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 注册缓存管理器，包含角色权限聚合缓存
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("roleMenuCodes", "roleMenus", "deptTree");
    }
}
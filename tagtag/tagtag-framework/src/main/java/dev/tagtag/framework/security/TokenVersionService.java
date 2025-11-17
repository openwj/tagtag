package dev.tagtag.framework.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 使用 Redis 维护用户令牌版本（ver），用于服务端主动失效旧令牌。
 * 约定：默认版本为 1；当用户注销或被禁用时提升版本，旧版本令牌全部失效。
 */
@Service
public class TokenVersionService {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数：注入 Redis 模板
     * @param stringRedisTemplate Redis 模板
     */
    public TokenVersionService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取用户当前令牌版本（默认 1）
     * @param userId 用户 ID
     * @return 当前版本号
     */
    public long getCurrentVersion(Long userId) {
        String key = buildKey(userId);
        String val = stringRedisTemplate.opsForValue().get(key);
        if (val == null) return 1L;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }

    /**
     * 提升用户令牌版本（使旧令牌全部失效）
     * @param userId 用户 ID
     * @return 新的版本号
     */
    public long bumpVersion(Long userId) {
        String key = buildKey(userId);
        Long next = stringRedisTemplate.opsForValue().increment(key);
        if (next == null) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(2L));
            return 2L;
        }
        return next;
    }

    /**
     * 校验令牌版本是否有效（令牌 ver 必须等于当前版本）
     * @param userId 用户 ID
     * @param tokenVer 令牌版本
     * @return 是否有效
     */
    public boolean isTokenVersionValid(Long userId, long tokenVer) {
        return tokenVer == getCurrentVersion(userId);
    }

    /**
     * 构造 Redis 键名
     * @param userId 用户 ID
     * @return 键名
     */
    private String buildKey(Long userId) {
        return "token:ver:" + (userId == null ? "null" : userId);
    }
}
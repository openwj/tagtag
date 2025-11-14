package dev.tagtag.module.auth.security;

import org.springframework.stereotype.Service;

/**
 * 用户详情服务占位（如接入 Spring Security 可在此加载用户）
 */
@Service
public class AuthUserDetailsService {
    /**
     * 根据用户名加载用户详情（未接入数据库，留作扩展）
     * @param username 用户名
     * @return 是否存在
     */
    public boolean exists(String username) { return username != null && !username.trim().isEmpty(); }
}

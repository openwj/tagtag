package dev.tagtag.module.auth.service;

import dev.tagtag.contract.auth.dto.TokenDTO;

/**
 * 认证服务接口，提供登录、刷新与注销
 */
public interface AuthService {

    /**
     * 用户登录（生成访问令牌与刷新令牌）
     * @param username 用户名
     * @param password 密码
     * @return 令牌对象
     */
    /**
     * 用户登录（生成访问令牌与刷新令牌）
     * @param username 用户名
     * @param password 密码
     * @return 令牌对象
     */
    TokenDTO login(String username, String password);

    /**
     * 刷新令牌（基于刷新令牌生成新的访问令牌与刷新令牌）
     * @param refreshToken 刷新令牌
     * @return 新令牌对象
     */
    TokenDTO refresh(String refreshToken);

    /**
     * 注销登录（对 JWT 为无状态操作，这里返回成功）
     * @param accessToken 访问令牌
     */
    void logout(String accessToken);

    /**
     * 用户注册（创建新用户并加密存储密码）
     * @param username 用户名
     * @param password 明文密码
     */
    void register(String username, String password);
}

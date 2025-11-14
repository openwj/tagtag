package dev.tagtag.contract.auth.api;

import dev.tagtag.common.model.Result;
import dev.tagtag.contract.auth.dto.TokenDTO;

/**
 * 登录认证契约接口，定义与认证相关的 RPC 方法
 */
public interface AuthApi {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 包含访问令牌与刷新令牌的结果
     */
    Result<TokenDTO> login(String username, String password);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌与刷新令牌
     */
    Result<TokenDTO> refresh(String refreshToken);

    /**
     * 注销登录
     *
     * @param accessToken 访问令牌
     * @return 操作结果
     */
    Result<Void> logout(String accessToken);
}

package dev.tagtag.module.auth.controller;

import dev.tagtag.common.model.Result;
import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.module.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器，提供登录、刷新与注销接口
 */
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录（返回访问令牌与刷新令牌）
     * @param req 登录请求
     * @return 令牌结果
     */
    @PostMapping("/login")
    public Result<TokenDTO> login(@Valid @RequestBody LoginRequest req) {
        TokenDTO dto = authService.login(req.getUsername(), req.getPassword());
        return Result.ok(dto);
    }

    /**
     * 刷新令牌（返回新的访问令牌与刷新令牌）
     * @param req 刷新请求
     * @return 令牌结果
     */
    @PostMapping("/refresh")
    public Result<TokenDTO> refresh(@Valid @RequestBody RefreshRequest req) {
        TokenDTO dto = authService.refresh(req.getRefreshToken());
        return Result.ok(dto);
    }

    /**
     * 注销登录（无状态 JWT 下返回成功）
     * @param req 注销请求
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req.getAccessToken());
        return Result.ok();
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class RefreshRequest {
        @NotBlank(message = "刷新令牌不能为空")
        private String refreshToken;
    }

    @Data
    public static class LogoutRequest {
        @NotBlank(message = "访问令牌不能为空")
        private String accessToken;
    }
}

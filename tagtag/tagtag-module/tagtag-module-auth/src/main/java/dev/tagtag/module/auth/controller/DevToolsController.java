package dev.tagtag.module.auth.controller;

import dev.tagtag.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.tagtag.common.constant.GlobalConstants;

/**
 * 开发环境辅助控制器（仅 dev profile 生效）
 */
@Profile("dev")
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/auth/tools")
@Tag(name = "开发工具", description = "开发环境辅助工具 API 接口")
public class DevToolsController {

    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数：注入密码编码器
     * @param passwordEncoder 密码编码器
     */
    public DevToolsController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 生成 BCrypt 密码哈希（开发辅助接口）
     * @param req 明文口令
     * @return BCrypt 哈希
     */
    @PostMapping("/bcrypt")
    @Operation(summary = "生成 BCrypt 密码哈希", description = "生成 BCrypt 密码哈希，用于开发环境辅助测试")
    public Result<String> bcrypt(@Valid @RequestBody BcryptRequest req) {
        String plain = req.getPlain() == null ? "" : req.getPlain().trim();
        String hash = passwordEncoder.encode(plain);
        return Result.ok(hash);
    }

    @Data
    public static class BcryptRequest {
        @NotBlank(message = "明文口令不能为空")
        private String plain;
    }
}
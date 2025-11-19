package dev.tagtag.module.auth.controller;

import dev.tagtag.common.model.Result;
import dev.tagtag.contract.auth.dto.CaptchaValidateRequest;
import dev.tagtag.contract.auth.dto.ImageCaptchaDTO;
import dev.tagtag.kernel.annotation.RateLimit;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.module.auth.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码控制器
 * 提供图片验证码生成与可选的预校验接口
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取图片验证码（带限流保护）
     * @return 图片验证码结果（DataURL 与 captchaId）
     */
    @RateLimit(key = "captcha:image", periodSeconds = 60, permits = 10, message = "验证码获取过于频繁，请稍后再试")
    @GetMapping("/image")
    public Result<ImageCaptchaDTO> image() {
        return Result.ok(captchaService.generateImageCaptcha());
    }

    /**
     * 预校验验证码（可用于前端在登录前主动检查）
     * @param req 校验请求，包含 captchaId 与 code
     * @return 是否通过
     */
    @PostMapping("/validate")
    public Result<Boolean> validate(@Valid @RequestBody CaptchaValidateRequest req) {
        boolean ok = captchaService.validate(req.getCaptchaId(), req.getCode());
        return Result.ok(ok);
    }
}
package dev.tagtag.contract.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码校验请求
 * 携带验证码标识与用户输入的验证码文本
 */
@Data
public class CaptchaValidateRequest {
    /**
     * 验证码唯一标识
     */
    @NotBlank(message = "captchaId 不能为空")
    private String captchaId;

    /**
     * 用户输入的验证码文本
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}
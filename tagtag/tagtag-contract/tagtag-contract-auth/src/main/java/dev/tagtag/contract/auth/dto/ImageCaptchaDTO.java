package dev.tagtag.contract.auth.dto;

import lombok.Data;

/**
 * 图片验证码返回结果
 * 提供验证码图片的 DataURL 以及对应的唯一标识 captchaId
 */
@Data
public class ImageCaptchaDTO {
    /**
     * 图片 DataURL（形如 data:image/png;base64,<...>）
     */
    private String src;
    /**
     * 验证码唯一标识，用于后续校验
     */
    private String captchaId;
}
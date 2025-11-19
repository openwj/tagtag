package dev.tagtag.module.auth.service;

import dev.tagtag.contract.auth.dto.ImageCaptchaDTO;

/**
 * 验证码服务接口
 * 提供图片验证码生成、校验与消费相关能力
 */
public interface CaptchaService {

    /**
     * 生成一次性图片验证码并写入缓存
     * @return 包含图片 DataURL 与 captchaId 的对象
     */
    ImageCaptchaDTO generateImageCaptcha();

    /**
     * 校验验证码但不删除（通常用于预校验）
     * @param captchaId 验证码唯一标识
     * @param code 用户输入验证码文本
     * @return 是否校验通过
     */
    boolean validate(String captchaId, String code);

    /**
     * 校验并消费验证码（成功后删除，防重复使用）
     *
     * @param captchaId 验证码唯一标识
     * @param code      用户输入验证码文本
     */
    void validateAndConsume(String captchaId, String code);
}
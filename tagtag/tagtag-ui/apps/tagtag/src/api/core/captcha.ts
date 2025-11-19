import { requestClient } from '#/api/request';

export interface ImageCaptchaResult {
  src: string;
  captchaId?: string;
}

/**
 * 获取图片验证码
 * 从后端拉取验证码图片与可选的 captchaId
 */
export async function fetchImageCaptchaApi() {
  return requestClient.get<ImageCaptchaResult>('/captcha/image');
}

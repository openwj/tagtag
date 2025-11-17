import { requestClient } from '#/api/request';

/**
 * 获取拼图滑块验证码挑战
 * @function getTranslateCaptcha
 */
export async function getTranslateCaptcha(): Promise<{
  challengeId: string;
  imageUrl: string;
  diffDistance?: number;
  pieceX: number;
  pieceY: number;
  expireAt?: number;
}> {
  return requestClient.get('/auth/captcha/translate/init');
}

/**
 * 提交拼图滑块验证码校验
 * @function verifyTranslateCaptcha
 * @param payload 校验参数
 */
export async function verifyTranslateCaptcha(payload: {
  challengeId: string;
  pieceX: number;
  moveX: number;
  time: number;
}): Promise<{
  passed: boolean;
  captchaToken?: string;
}> {
  return requestClient.post('/auth/captcha/translate/verify', payload);
}

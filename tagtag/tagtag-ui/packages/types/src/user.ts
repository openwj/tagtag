import type { BasicUserInfo } from '@vben-core/typings';

/** 用户信息 */
interface UserInfo extends BasicUserInfo {
  /**
   * 用户描述
   */
  desc: string;
  /**
   * 首页地址
   */
  homePath: string;

  /**
   * accessToken
   */
  token: string;

  /**
   * 电子邮箱
   */
  email?: string;

  /**
   * 手机号码
   */
  phone?: string;

  /**
   * 性别（0:未知, 1:男, 2:女）
   */
  gender?: number;

  /**
   * 生日 (YYYY-MM-DD)
   */
  birthday?: string;
}

export type { UserInfo };

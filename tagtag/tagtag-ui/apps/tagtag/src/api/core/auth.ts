import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  /** 登录接口返回值 */
  export interface LoginResult {
    accessToken: string;
    refreshToken?: string;
    tokenType?: string;
    expiresIn?: number;
  }

  export interface RefreshTokenParams {
    refreshToken: string;
  }

  export interface RegisterParams {
    username: string;
    password: string;
  }
}

/**
 * 登录
 * Perform user login with credentials and optional captcha token
 * @param data 登录参数，包含 `username`/`password` 及可选 `captchaToken`
 * @returns 登录结果，包含 `accessToken` 与可选 `refreshToken`
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<AuthApi.LoginResult>('/auth/login', data);
}

/**
 * 刷新 accessToken
 * Refresh access token using a persisted refresh token
 * @param data 刷新参数，包含 `refreshToken`
 * @returns 新的令牌信息，含 `accessToken` 与可选 `refreshToken`
 */
export async function refreshTokenApi(data: AuthApi.RefreshTokenParams) {
  return requestClient.post<AuthApi.LoginResult>('/auth/refresh', data);
}

/**
 * 退出登录
 * Logout current user from server side
 * @param accessToken 现有的访问令牌，用于服务端注销校验
 * @returns 服务端返回的结果（一般为空）
 */
export async function logoutApi(accessToken: string) {
  return requestClient.post('/auth/logout', { accessToken });
}

/**
 * 获取用户权限码
 * Fetch access codes (permissions) of current user
 * @returns 权限码字符串数组
 */
export async function getAccessCodesApi() {
  return requestClient.get<string[]>('/auth/codes');
}

/**
 * 注册账号
 * Register a new account with username and password
 * @param data 注册参数，包含 `username`/`password`
 * @returns 空响应
 */
export async function registerApi(data: AuthApi.RegisterParams) {
  return requestClient.post<void>('/auth/register', data);
}

/**
 * 获取当前用户信息
 * Fetch current user profile from server
 * @returns 任意结构的用户信息（由后端定义）
 */
export async function meApi() {
  return requestClient.get<any>('/auth/me');
}

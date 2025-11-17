import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
    captchaToken?: string;
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
 */
/**
 * 登录
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<AuthApi.LoginResult>('/auth/login', data);
}

/**
 * 刷新accessToken
 */
/**
 * 刷新accessToken
 */
export async function refreshTokenApi(data: AuthApi.RefreshTokenParams) {
  return requestClient.post<AuthApi.LoginResult>('/auth/refresh', data);
}

/**
 * 退出登录
 */
/**
 * 退出登录
 */
export async function logoutApi(accessToken: string) {
  return requestClient.post('/auth/logout', { accessToken });
}

/**
 * 获取用户权限码
 */
export async function getAccessCodesApi() {
  return requestClient.get<string[]>('/auth/codes');
}

/**
 * 注册账号
 */
export async function registerApi(data: AuthApi.RegisterParams) {
  return requestClient.post<void>('/auth/register', data);
}

/**
 * 获取当前用户信息
 */
export async function meApi() {
  return requestClient.get<any>('/auth/me');
}

import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户信息
 */
/**
 * 获取当前用户信息（后端 /auth/me）并转换为 UserInfo 结构
 */
export async function getUserInfoApi() {
  const data = await requestClient.get<any>('/auth/me');
  const userInfo: UserInfo = {
    id: data?.id ?? 0,
    username: data?.username ?? '',
    realName: data?.nickname ?? data?.username ?? '',
    nickname: data?.nickname ?? '',
    roles: ['user'],
    homePath: '/analytics',
    // password 字段前端不使用
    password: '',
    desc: data?.remark ?? data?.desc ?? '',
    token: data?.token ?? '',
    avatar: data?.avatar ?? '',
    userId: data?.userId ?? 0,
    email: data?.email ?? '',
    phone: data?.phone ?? '',
    gender: data?.gender ?? 0,
    birthday: data?.birthday ?? '',
  } as UserInfo;
  return userInfo;
}

import type { RouteRecordStringComponent } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户所有菜单（后端已返回路由记录）
 */
export async function getAllMenusApi() {
  return requestClient.get<RouteRecordStringComponent[]>('/auth/menu/all');
}

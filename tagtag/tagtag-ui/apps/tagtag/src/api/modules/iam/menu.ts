import { requestClient } from '#/api/request';

export namespace MenuApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ asc: boolean; field: string }>;
  }
  export interface MenuQuery {
    menuCode?: string;
    menuName?: string;
  }
  /** 菜单表单类型 */
  export interface MenuForm {
    id?: number | string;
    menuCode: string;
    menuName: string;
    parentId?: number | string;
    path?: string;
    component?: string;
    icon?: string;
    sort?: number;
    status?: number;
    menuType?: number; // 0目录 1菜单 2按钮
    hideInMenu?: boolean;
    keepAlive?: boolean;
    link?: string;
    remark?: string;
  }
}

const Api = {
  BaseApi: `/iam/menus`,
};

/**
 * 获取菜单详情
 * @param id 菜单ID
 */
export async function getMenuById(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
}

// 删除重复别名方法：保持单一 getMenuById

/**
 * 菜单树查询（不分页）
 * @param query 菜单查询条件
 */
export async function getMenuTree(
  query: MenuApiParams.MenuQuery & { menuType?: number; status?: number } = {},
) {
  return requestClient.get(`${Api.BaseApi}/tree`, { params: { ...query } });
}

/**
 * 新增菜单
 * @param data 菜单表单数据
 */
export function addMenu(data: MenuApiParams.MenuForm) {
  return requestClient.post(`${Api.BaseApi}`, data);
}

/**
 * 编辑菜单
 * @param data 菜单表单数据（包含 id）
 */
export function editMenu(data: MenuApiParams.MenuForm) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

// 删除重复别名方法：保持单一 editMenu

/**
 * 删除菜单
 * @param id 菜单ID
 */
export function deleteMenu(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}

/**
 * 批量删除菜单
 * @param ids 菜单ID列表
 */
export function batchDeleteMenu(ids: Array<number | string>) {
  return requestClient.delete(`${Api.BaseApi}/batch`, { data: { ids } });
}

/**
 * 更新单条菜单状态
 * @param id 菜单ID
 * @param status 状态（0=禁用，1=启用）
 */
export function updateMenuStatus(id: number | string, status: number) {
  return requestClient.put(`${Api.BaseApi}/${id}/status`, { status });
}

/**
 * 批量更新菜单状态
 * @param ids 菜单ID列表
 * @param status 状态（0=禁用，1=启用）
 */
export function batchUpdateMenuStatus(
  ids: Array<number | string>,
  status: number,
) {
  return requestClient.put(`${Api.BaseApi}/status/batch`, { ids, status });
}

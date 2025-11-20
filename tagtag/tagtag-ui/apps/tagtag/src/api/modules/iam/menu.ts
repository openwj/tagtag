import { requestClient } from '#/api/request';

export namespace MenuApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ field: string; asc: boolean }>;
  }
  export interface MenuQuery {
    menuCode?: string;
    menuName?: string;
  }
}

const Api = {
  BaseApi: `/iam/menus`,
};

/**
 * 菜单分页查询
 * @param query 菜单查询条件
 * @param page 分页与排序参数
 */
export async function getMenuPage(query: MenuApiParams.MenuQuery, page: MenuApiParams.PageQuery) {
  return requestClient.post(`${Api.BaseApi}/page`, { query, page });
}

/**
 * 根据父ID查询子菜单列表
 * @param parentId 父菜单ID
 */
export async function listMenusByParent(parentId: number | string) {
  return requestClient.get(`${Api.BaseApi}/parent/${parentId}`);
}

/**
 * 获取菜单详情
 * @param id 菜单ID
 */
export async function getMenuById(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
}

/**
 * 根据菜单编码查询单条
 * @param menuCode 菜单编码
 */
export async function getMenuByCode(menuCode: string) {
  return requestClient.get(`${Api.BaseApi}/code/${menuCode}`);
}

/**
 * 菜单树查询（不分页）
 * @param query 菜单查询条件
 */
export async function getMenuTree(query: MenuApiParams.MenuQuery & { status?: number; menuType?: number } = {}) {
  return requestClient.get(`${Api.BaseApi}/tree`, { params: { ...query } });
}

/**
 * 判断菜单编码是否存在
 * @param menuCode 菜单编码
 */
export async function existsByCode(menuCode: string) {
  return requestClient.get(`${Api.BaseApi}/exist/code/${menuCode}`);
}

/**
 * 新增菜单
 * @param data 菜单表单数据
 */
export function addMenu(data: Record<string, any>) {
  return requestClient.post(`${Api.BaseApi}`, data);
}

/**
 * 编辑菜单
 * @param data 菜单表单数据（包含 id）
 */
export function editMenu(data: Record<string, any>) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

/**
 * 删除菜单
 * @param id 菜单ID
 */
export function deleteMenu(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}
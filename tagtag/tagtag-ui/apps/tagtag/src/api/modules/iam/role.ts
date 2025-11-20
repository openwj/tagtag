import { requestClient } from '#/api/request';

export namespace RoleApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ field: string; asc: boolean }>;
  }
  export interface RoleQuery {
    code?: string;
    name?: string;
    status?: number;
  }
  export interface RoleForm {
    id?: number;
    code?: string;
    name?: string;
    status?: number;
  }
}

const Api = {
  BaseApi: `/iam/roles`,
};

/**
 * 角色分页查询
 * @param query 角色查询条件
 * @param page 分页与排序参数
 */
export async function getRolePage(
  query: RoleApiParams.RoleQuery,
  page: RoleApiParams.PageQuery,
) {
  return requestClient.post(`${Api.BaseApi}/page`, { query, page });
}

/**
 * 获取角色详情
 * @param id 角色ID
 */
export async function getRoleById(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
}

/**
 * 新增角色
 * @param data 角色表单数据
 */
export function addRole(data: RoleApiParams.RoleForm) {
  return requestClient.post(`${Api.BaseApi}`, data);
}

/**
 * 编辑角色
 * @param data 角色表单数据（包含 id）
 */
export function editRole(data: RoleApiParams.RoleForm) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

/**
 * 删除角色
 * @param id 角色ID
 */
export function deleteRole(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}

/**
 * 查询角色已分配的菜单（含按钮）
 * @param id 角色ID
 */
export async function listRoleMenus(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}/menus`);
}

/**
 * 为角色分配菜单（覆盖式）
 * @param id 角色ID
 * @param menuIds 菜单ID列表
 */
export function assignRoleMenus(id: number | string, menuIds: Array<number>) {
  return requestClient.post(`${Api.BaseApi}/${id}/menus`, menuIds);
}

/**
 * 判断角色编码是否存在
 * @param code 角色编码
 */
export async function existsByCode(code: string) {
  return requestClient.get(`${Api.BaseApi}/exist/code/${code}`);
}

/**
 * 判断角色名称是否存在
 * @param name 角色名称
 */
export async function existsByName(name: string) {
  return requestClient.get(`${Api.BaseApi}/exist/name/${name}`);
}
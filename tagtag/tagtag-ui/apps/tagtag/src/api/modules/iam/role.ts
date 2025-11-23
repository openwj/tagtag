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
    roleType?: number;
    sort?: number;
    remark?: string;
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
export async function getRoleDetail(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
}

/**
 * 根据角色编码查询详情
 * @param code 角色编码
 * @returns 角色详情
 */
export async function getRoleByCode(code: string) {
  return requestClient.get(`${Api.BaseApi}/code/${code}`);
}

/**
 * 根据角色名称查询详情
 * @param name 角色名称
 * @returns 角色详情
 */
export async function getRoleByName(name: string) {
  return requestClient.get(`${Api.BaseApi}/name/${name}`);
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
 * 查询角色已分配的菜单ID列表（包含目录/菜单/按钮）
 * @param id 角色ID
 */
export async function listRoleMenuIds(id: number | string) {
  return requestClient.get<Array<number>>(`${Api.BaseApi}/${id}/menu-ids`);
}

/**
 * 批量删除角色
 * @param ids 角色ID列表
 */
export function batchDeleteRole(ids: Array<number | string>) {
  return requestClient.delete(`${Api.BaseApi}/batch`, { data: { ids } });
}

/**
 * 更新单条角色状态
 * @param id 角色ID
 * @param disabled 是否禁用（true=禁用，false=启用）
 */
export function updateRoleStatus(id: number | string, disabled: boolean) {
  return requestClient.put(`${Api.BaseApi}/${id}/status`, undefined, {
    params: { disabled },
  });
}

/**
 * 批量更新角色状态
 * @param ids 角色ID列表
 * @param disabled 是否禁用（true=禁用，false=启用）
 */
export function batchUpdateRoleStatus(
  ids: Array<number | string>,
  disabled: boolean,
) {
  return requestClient.put(`${Api.BaseApi}/status/batch`, { ids, disabled });
}

/**
 * 判断角色编码是否存在
 * @param code 角色编码
 */
// 删除未使用：编码唯一性校验（页面未调用）

/**
 * 判断角色名称是否存在
 * @param name 角色名称
 */
// 删除未使用：名称唯一性校验（页面未调用）

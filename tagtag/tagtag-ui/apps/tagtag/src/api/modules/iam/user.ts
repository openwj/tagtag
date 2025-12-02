import { requestClient } from '#/api/request';

export namespace UserApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ asc: boolean; field: string }>;
  }
  export interface UserQuery {
    username?: string;
    nickname?: string;
    status?: number;
    gender?: number;
    roleId?: number;
    email?: string;
    phone?: string;
    deptId?: number;
    employeeNo?: string;
    createTimeRange?: { end?: string; start?: string };
    entryDateRange?: { end?: string; start?: string };
  }
  export interface UserForm {
    id?: number;
    username?: string;
    password?: string;
    nickname?: string;
    email?: string;
    phone?: string;
    gender?: number;
    deptId?: number;
    status?: number;
    roleIds?: number[];
    avatar?: string;
    remark?: string;
    employeeNo?: string;
    position?: string;
    birthday?: string;
    entryDate?: string;
  }
}

const Api = {
  BaseApi: `/iam/users`,
};

/**
 * 用户分页查询
 * @param query 用户查询条件
 * @param page 分页与排序参数
 */
export async function getUserPage(
  query: UserApiParams.UserQuery,
  page: UserApiParams.PageQuery,
) {
  return requestClient.post(`${Api.BaseApi}/page`, { query, page });
}

/**
 * 新增用户
 * @param data 用户表单数据
 */
export function addUser(data: UserApiParams.UserForm) {
  return requestClient.post(`${Api.BaseApi}`, data);
}

/**
 * 编辑用户
 * @param data 用户表单数据（包含 id）
 */
export function editUser(data: UserApiParams.UserForm) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

/**
 * 删除用户
 * @param id 用户ID
 */
export function deleteUser(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}

/**
 * 为用户分配角色（覆盖式）
 * @param id 用户ID
 * @param roleIds 角色ID列表
 */
export function assignUserRoles(id: number | string, roleIds: Array<number>) {
  return requestClient.post(`${Api.BaseApi}/${id}/roles`, roleIds);
}

/**
 * 批量为用户分配角色（覆盖式）
 * @param userIds 用户ID列表
 * @param roleIds 角色ID列表
 */
export function batchAssignRoles(
  userIds: Array<number | string>,
  roleIds: Array<number>,
) {
  return requestClient.post(`${Api.BaseApi}/roles/batch`, { userIds, roleIds });
}

/**
 * 更新单个用户状态
 * @param id 用户ID
 * @param status 用户状态（0=禁用，1=启用）
 */
export function updateUserStatus(id: number | string, status: number) {
  return requestClient.put(`${Api.BaseApi}/${id}/status`, { status });
}

/**
 * 批量更新用户状态
 * @param ids 用户ID列表
 * @param status 用户状态（0=禁用，1=启用）
 */
export function batchUpdateUserStatus(
  ids: Array<number | string>,
  status: number,
) {
  return requestClient.put(`${Api.BaseApi}/status/batch`, { ids, status });
}

/**
 * 批量删除用户
 * @param ids 用户ID列表
 */
export function batchDeleteUsers(ids: Array<number | string>) {
  return requestClient.delete(`${Api.BaseApi}/batch`, { data: { ids } });
}

/**
 * 重置用户密码
 * @param id 用户ID
 * @param password 新密码
 */
export function resetUserPassword(id: number | string, password: string) {
  return requestClient.put(`${Api.BaseApi}/${id}/password`, { password });
}

/**
 * 查询用户已分配的角色
 * @param id 用户ID
 * @returns 角色列表
 */
export function getUserRoles(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}/roles`);
}

/**
 * 本人修改密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export function changeMyPassword(oldPassword: string, newPassword: string) {
  return requestClient.put(`${Api.BaseApi}/me/password`, {
    oldPassword,
    newPassword,
  });
}

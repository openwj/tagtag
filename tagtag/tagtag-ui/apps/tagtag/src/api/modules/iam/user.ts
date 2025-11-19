import { requestClient } from '#/api/request';

export namespace UserApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ field: string; asc: boolean }>;
  }
  export interface UserQuery {
    username?: string;
    nickname?: string;
    status?: number;
    gender?: number;
    roleId?: number;
    email?: string;
    mobile?: string;
    deptId?: number;
    employeeNo?: string;
    createTimeRange?: { start?: string; end?: string };
    entryDateRange?: { start?: string; end?: string };
  }
  export interface UserForm {
    id?: number;
    username?: string;
    password?: string;
    nickname?: string;
    email?: string;
    mobile?: string;
    gender?: number;
    deptId?: number;
    status?: number;
    roleIds?: number[];
    avatar?: string;
    remark?: string;
    employeeNo?: string;
    jobTitle?: string;
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
export async function getUserPage(query: UserApiParams.UserQuery, page: UserApiParams.PageQuery) {
  return requestClient.post(`${Api.BaseApi}/page`, { query, page });
}

/**
 * 获取用户详情
 * @param id 用户ID
 */
export async function getUserById(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
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
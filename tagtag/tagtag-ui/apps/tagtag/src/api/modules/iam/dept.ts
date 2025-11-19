import { requestClient } from '#/api/request';

export namespace ApiParams {
  export interface PageFetchParams {
    [key: string]: any;
  }
  export interface FetchParams {
    [key: string]: any;
  }
}

const Api = {
  BaseApi: `/iam/depts`,
};

/**
 * 获取部门Tree
 */
export async function getDeptTree(params: ApiParams.PageFetchParams) {
  return requestClient.get(`${Api.BaseApi}/tree`, { params });
}

/**
 * 查询部门详情
 */
export async function getDeptById(id: number | string) {
  return requestClient.get(`${Api.BaseApi}/${id}`);
}

/**
 * 分页查询部门列表
 */
export async function getDeptPage(params: ApiParams.PageFetchParams) {
  return requestClient.get(`${Api.BaseApi}/page`, { params });
}

/**
 * 根据部门名称模糊查询
 */
export async function searchDeptByName(params: { name: string }) {
  return requestClient.get(`${Api.BaseApi}/search`, { params });
}

/**
 * 获取部门路径
 */
export async function getDeptPath(deptId: number | string) {
  return requestClient.get(`${Api.BaseApi}/path/${deptId}`);
}

/**
 * 获取部门层级深度
 */
export async function getDeptLevel(deptId: number | string) {
  return requestClient.get(`${Api.BaseApi}/level/${deptId}`);
}

/**
 * 统计部门下的用户数量
 */
export async function countDeptUsers(deptId: number | string) {
  return Promise.reject(new Error('countDeptUsers 未实现：后端暂不支持'));
}

/**
 * 查询子部门列表
 */
export async function getDeptChildren(deptId: number | string) {
  return Promise.reject(new Error('getDeptChildren 未实现：后端暂不支持'));
}

/**
 * 获取部门的所有子部门ID
 */
export async function getDeptChildrenIds(deptId: number | string) {
  return requestClient.get(`${Api.BaseApi}/children/ids/${deptId}`);
}

/**
 * 新增部门
 */
export function addDept(data: ApiParams.FetchParams) {
  return requestClient.post(`${Api.BaseApi}`, data);
}

/**
 * 编辑部门
 */
export function editDept(data: ApiParams.FetchParams) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

/**
 * 删除部门
 */
export function deleteDept(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}

/**
 * 更新部门状态
 */
/**
 * 更新部门状态
 * @param data 包含部门 `id` 与 `status(0|1)`
 */
export function updateDeptStatus(data: {
  id: number | string;
  status: number;
}) {
  return requestClient.put(`${Api.BaseApi}`, data);
}

/**
 * 批量更新部门状态
 */
export function batchUpdateDeptStatus(data: {
  ids: (number | string)[];
  status: number;
}) {
  return Promise.reject(new Error('batchUpdateDeptStatus 未实现：后端暂不支持'));
}

/**
 * 移动部门
 */
export function moveDept(data: {
  id: number | string;
  targetParentId: number | string;
}) {
  return Promise.reject(new Error('moveDept 未实现：后端暂不支持'));
}

/**
 * 检查部门编码是否存在
 */
export function checkDeptCode(params: {
  code: string;
  excludeId?: number | string;
}) {
  return requestClient.get(`${Api.BaseApi}/check/code`, { params });
}

/**
 * 检查部门是否有用户
 */
export function checkDeptHasUsers(deptId: number | string) {
  return Promise.reject(new Error('checkDeptHasUsers 未实现：后端暂不支持'));
}

/**
 * 检查部门是否有子部门
 */
export function checkDeptHasChildren(deptId: number | string) {
  return Promise.reject(new Error('checkDeptHasChildren 未实现：后端暂不支持'));
}

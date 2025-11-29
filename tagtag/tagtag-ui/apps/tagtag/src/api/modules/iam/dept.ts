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
 * 统计部门下的用户数量
 */
// 删除未实现且未使用的方法：保持最小接口集合

/**
 * 查询子部门列表
 */
// 删除未实现且未使用的方法：保持最小接口集合

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
 * @param id 部门ID
 * @param status 状态（0=禁用，1=启用）
 */
export function updateDeptStatus(id: number | string, status: number) {
  return requestClient.put(`${Api.BaseApi}/${id}/status`, { status });
}

/**
 * 移动部门
 */
// 删除未实现且未使用的方法：保持最小接口集合

/**
 * 检查部门是否有用户
 */
// 删除未实现且未使用的方法：保持最小接口集合

/**
 * 检查部门是否有子部门
 */
// 删除未实现且未使用的方法：保持最小接口集合

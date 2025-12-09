import { requestClient } from '#/api/request';

export namespace FileApiParams {
  export interface PageQuery {
    pageNo?: number;
    pageSize?: number;
    sortFields?: Array<{ asc: boolean; field: string }>;
  }
  export interface FileQuery {
    name?: string;
    mimeType?: string;
    ext?: string;
    storageType?: string;
    createTimeRange?: { start?: string; end?: string };
  }
}

const Api = {
  BaseApi: `/storage/files`,
};

/**
 * 分页查询文件
 * @param query 查询条件
 * @param page 分页与排序参数
 */
export async function getFilePage(
  query: FileApiParams.FileQuery,
  page: FileApiParams.PageQuery,
) {
  return requestClient.post(`${Api.BaseApi}/page`, { query, page });
}

/**
 * 上传文件
 * @param file 文件对象
 */
export async function uploadFile(file: File) {
  return requestClient.upload(`${Api.BaseApi}/upload`, { file });
}

/**
 * 删除单个文件
 * @param id 文件ID
 */
export function deleteFile(id: number | string) {
  return requestClient.delete(`${Api.BaseApi}/${id}`);
}

/**
 * 批量删除
 * @param ids 文件ID数组
 */
export function batchDeleteFiles(ids: Array<number | string>) {
  return requestClient.delete(`${Api.BaseApi}/batch`, { data: { ids } });
}

/**
 * 通过 publicId 授权下载文件为 Blob
 * @param publicId 文件公共ID
 * @returns 文件二进制流（Blob）
 */
export async function downloadByPublicId(publicId: string) {
  return requestClient.download(`${Api.BaseApi}/${publicId}/download`);
}

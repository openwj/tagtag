package dev.tagtag.contract.storage.api;

import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.model.BatchIdsDTO;
import dev.tagtag.contract.storage.dto.FileDTO;
import dev.tagtag.contract.storage.dto.FilePageQuery;
import dev.tagtag.contract.storage.dto.FileUploadResult;
import dev.tagtag.common.model.PageQuery;

/**
 * 文件管理契约接口，定义与存储相关的 RPC 方法
 */
public interface FileApi {

    /**
     * 分页查询文件
     * @param query 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    Result<PageResult<FileDTO>> page(FilePageQuery query, PageQuery page);

    /**
     * 上传文件
     */
    Result<FileUploadResult> upload(byte[] content, String filename, String contentType);

    /**
     * 删除单个文件
     * @param id 文件ID
     * @return 操作结果
     */
    Result<Void> delete(Long id);

    /**
     * 批量删除文件
     * @param ids 批量ID
     * @return 操作结果
     */
    Result<Void> batchDelete(BatchIdsDTO ids);

    /**
     * 查询文件元信息
     * @param id 文件ID
     * @return 文件 DTO
     */
    Result<FileDTO> getById(Long id);
}

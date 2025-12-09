package dev.tagtag.module.storage.api.impl;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.storage.api.FileApi;
import dev.tagtag.common.model.BatchIdsDTO;
import dev.tagtag.contract.storage.dto.FileDTO;
import dev.tagtag.contract.storage.dto.FilePageQuery;
import dev.tagtag.contract.storage.dto.FileUploadResult;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.module.storage.entity.FileResource;
import dev.tagtag.module.storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 文件契约实现
 */
@Service
@RequiredArgsConstructor
public class FileApiImpl implements FileApi {

    private final FileService fileService;

    /** 分页查询文件 */
    @Override
    public Result<PageResult<FileDTO>> page(FilePageQuery query, PageQuery page) {
        return Result.ok(fileService.pageFiles(query, page));
    }

    /** 上传文件 */
    @Override
    public Result<FileUploadResult> upload(byte[] content, String filename, String contentType) {
        try {
            FileResource fr = fileService.uploadLocal(content, filename, contentType);
            FileUploadResult res = new FileUploadResult()
                .setId(fr.getId())
                .setUrl(fr.getUrl())
                .setName(fr.getName())
                .setOriginalName(fr.getOriginalName())
                .setSize(fr.getSize())
                .setMimeType(fr.getMimeType());
            return Result.ok(res);
        } catch (Exception e) {
            return Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /** 删除单个文件 */
    @Override
    public Result<Void> delete(Long id) {
        fileService.removeById(id);
        return Result.ok();
    }

    /** 批量删除 */
    @Override
    public Result<Void> batchDelete(BatchIdsDTO ids) {
        fileService.removeBatchByIds(ids.getIds());
        return Result.ok();
    }

    /** 查询文件元信息 */
    @Override
    public Result<FileDTO> getById(Long id) {
        FileResource fr = fileService.getById(id);
        if (fr == null) {
            return Result.fail(ErrorCode.NOT_FOUND);
        }
        return Result.ok(fileService.toDTO(fr));
    }
}

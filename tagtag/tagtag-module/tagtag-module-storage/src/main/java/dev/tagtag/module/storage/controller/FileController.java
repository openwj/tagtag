package dev.tagtag.module.storage.controller;

import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.model.BatchIdsDTO;
import dev.tagtag.contract.storage.dto.FileDTO;
import dev.tagtag.contract.storage.dto.FilePageQuery;
import dev.tagtag.contract.storage.dto.FileUploadResult;
import dev.tagtag.module.storage.entity.FileResource;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.module.storage.service.FileService;
import dev.tagtag.framework.security.annotation.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/storage/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "存储管理", description = "文件存储相关 API 接口")
public class FileController {

    private final FileService fileService;
    

    /**
     * 文件分页查询
     * @param req 通用分页请求体，包含查询条件与分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "文件分页查询", description = "根据条件分页查询文件列表")
    public Result<PageResult<FileDTO>> page(@RequestBody dev.tagtag.common.model.PageRequest<FilePageQuery> req) {
        PageResult<FileDTO> pr = fileService.pageFiles(req.query(), req.page());
        return Result.ok(pr);
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传文件到本地存储")
    public Result<FileUploadResult> upload(@RequestPart("file") MultipartFile file) throws Exception {
        FileResource fr = fileService.uploadLocal(file);
        FileUploadResult res = new FileUploadResult()
                .setId(fr.getId())
                .setUrl(fr.getUrl())
                .setName(fr.getName())
                .setOriginalName(fr.getOriginalName())
                .setSize(fr.getSize())
                .setMimeType(fr.getMimeType());
        return Result.ok(res);
    }

    

    /**
     * 删除单个文件
     * @param id 文件ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除单个文件", description = "根据文件ID删除文件")
    public Result<Void> delete(@PathVariable("id") Long id) {
        fileService.removeById(id);
        return Result.ok();
    }

    /**
     * 批量删除
     * @param req ids
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件", description = "批量删除文件")
    public Result<Void> batchDelete(@RequestBody BatchIdsDTO req) {
        fileService.removeBatchByIds(req.getIds());
        return Result.ok();
    }

    /**
     * 预览文件（用于图片等），不强制下载，支持缓存
     * @param publicId 文件PublicID
     * @return 二进制流（Inline）
     */
    @GetMapping("/view/{publicId}")
    @Operation(summary = "预览文件", description = "预览文件，用于图片等，不强制下载，支持缓存")
    public ResponseEntity<Resource> view(@PathVariable("publicId") String publicId) {
        FileResource fr = this.fileService.lambdaQuery().eq(FileResource::getPublicId, publicId).one();
        if (fr == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(fr.getPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        
        // 缓存 30 天
        long cacheAge = 30 * 24 * 3600; 
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=" + cacheAge)
                .contentType(MediaType.parseMediaType(fr.getMimeType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fr.getMimeType()))
                .body(resource);
    }

    /**
     * 下载文件
     * @param id 文件ID
     * @return 二进制流
     */
    @GetMapping("/{publicId}/download")
    @RequirePerm(Permissions.FILE_DOWNLOAD)
    @Operation(summary = "下载文件", description = "根据文件PublicID下载文件")
    public ResponseEntity<Resource> download(@PathVariable("publicId") String publicId) {
        FileResource fr = this.fileService.lambdaQuery().eq(FileResource::getPublicId, publicId).one();
        if (fr == null) {
            log.warn("download: file not found by publicId={}", publicId);
            return ResponseEntity.notFound().build();
        }
        File file = new File(fr.getPath());
        if (!file.exists()) {
            log.warn("download: path missing for publicId={}, path={}", publicId, fr.getPath());
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String filename = fr.getOriginalName() == null ? fr.getName() : fr.getOriginalName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(fr.getMimeType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fr.getMimeType()))
                .body(resource);
    }

    

    
}

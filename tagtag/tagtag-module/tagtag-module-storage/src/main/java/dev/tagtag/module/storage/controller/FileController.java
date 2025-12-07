package dev.tagtag.module.storage.controller;

import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.storage.dto.BatchIdsDTO;
import dev.tagtag.contract.storage.dto.FileDTO;
import dev.tagtag.contract.storage.dto.FilePageQuery;
import dev.tagtag.contract.storage.dto.FileUploadResult;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.module.storage.entity.FileResource;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.module.storage.service.FileService;
import dev.tagtag.framework.security.RequirePerm;
import dev.tagtag.framework.security.JwtService;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/storage/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;

    /**
     * 文件分页查询
     * @param query 查询条件
     * @param page 分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    public Result<PageResult<FileDTO>> page(@RequestBody ReqPage req) {
        PageResult<FileDTO> pr = fileService.pageFiles(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /**
     * 上传文件
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
     * 生成短期下载令牌并返回临时下载链接
     * @param publicId 文件公共ID
     */
    @PostMapping("/{publicId}/download-token")
    @RequirePerm(Permissions.FILE_DOWNLOAD)
    public Result<Map<String, String>> generateDownloadToken(@PathVariable("publicId") String publicId) {
        FileResource fr = this.fileService.lambdaQuery().eq(FileResource::getPublicId, publicId).one();
        if (fr == null) {
            return Result.fail(ErrorCode.NOT_FOUND, "文件不存在");
        }
        String token = jwtService.generateToken(Map.of("fid", publicId), "file-download", 300);
        String url = "/api/storage/files/download?token=" + token;
        return Result.ok(Map.of("token", token, "url", url));
    }

    /**
     * 删除单个文件
     * @param id 文件ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
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
    public Result<Void> batchDelete(@RequestBody BatchIdsDTO req) {
        fileService.removeBatchByIds(req.getIds());
        return Result.ok();
    }

    /**
     * 下载文件
     * @param id 文件ID
     * @return 二进制流
     */
    @GetMapping("/{publicId}/download")
    @RequirePerm(Permissions.FILE_DOWNLOAD)
    public ResponseEntity<Resource> download(@PathVariable("publicId") String publicId) {
        FileResource fr = this.fileService.lambdaQuery().eq(FileResource::getPublicId, publicId).one();
        if (fr == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(fr.getPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String filename = fr.getOriginalName() == null ? fr.getName() : fr.getOriginalName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(fr.getMimeType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fr.getMimeType()))
                .body(resource);
    }

    /**
     * 基于令牌的下载接口（令牌有效期 5 分钟）
     * @param token 下载令牌
     * @return 文件流
     */
    @GetMapping("/download")
    @RequirePerm(Permissions.FILE_DOWNLOAD)
    public ResponseEntity<Resource> downloadByToken(@RequestParam("token") String token) {
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(403).build();
        }
        String fid = String.valueOf(jwtService.getClaims(token).getOrDefault("fid", ""));
        if (fid.isBlank()) {
            return ResponseEntity.status(400).build();
        }
        FileResource fr = this.fileService.lambdaQuery().eq(FileResource::getPublicId, fid).one();
        if (fr == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(fr.getPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String filename = fr.getOriginalName() == null ? fr.getName() : fr.getOriginalName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(fr.getMimeType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : fr.getMimeType()))
                .body(resource);
    }

    /**
     * 请求包装：分页与查询条件
     */
    public static class ReqPage {
        private FilePageQuery query;
        private PageQuery page;

        public FilePageQuery getQuery() { return query; }
        public void setQuery(FilePageQuery query) { this.query = query; }
        public PageQuery getPage() { return page; }
        public void setPage(PageQuery page) { this.page = page; }
    }
}

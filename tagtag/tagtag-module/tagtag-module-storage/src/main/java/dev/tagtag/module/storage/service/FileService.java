package dev.tagtag.module.storage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.storage.dto.FileDTO;
import dev.tagtag.contract.storage.dto.FilePageQuery;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.framework.util.PageResults;
import java.util.UUID;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.module.storage.entity.FileResource;
import dev.tagtag.module.storage.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * 文件服务，封装分页、上传、删除等核心逻辑
 */
@Service
@RequiredArgsConstructor
public class FileService extends ServiceImpl<FileMapper, FileResource> {

    @Value("${storage.local.base-path:uploads}")
    private String basePath;

    /**
     * 分页查询文件
     * @param query 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果（DTO）
     */
    public PageResult<FileDTO> pageFiles(FilePageQuery query, PageQuery pageQuery) {
        LambdaQueryWrapper<FileResource> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(FileResource::getName, query.getName());
        }
        if (StringUtils.hasText(query.getMimeType())) {
            wrapper.like(FileResource::getMimeType, query.getMimeType());
        }
        if (StringUtils.hasText(query.getExt())) {
            wrapper.eq(FileResource::getExt, query.getExt());
        }
        if (StringUtils.hasText(query.getStorageType())) {
            wrapper.eq(FileResource::getStorageType, query.getStorageType());
        }
        Page<FileResource> page = this.page(Pages.toPage(pageQuery), wrapper);
        PageResult<FileResource> pr = PageResults.of(page);
        return pr.map(this::toDTO);
    }

    /**
     * 上传文件到本地并入库
     * @param file MultipartFile
     * @return 保存后的实体
     */
    public FileResource uploadLocal(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains(".")) ? originalName.substring(originalName.lastIndexOf('.') + 1) : "";
        String mime = file.getContentType();

        // 构建存储路径
        Path root = Paths.get(basePath).toAbsolutePath();
        Files.createDirectories(root);
        String fname = System.currentTimeMillis() + "_" + (originalName == null ? "file" : originalName);
        Path target = root.resolve(fname);
        Files.copy(file.getInputStream(), target);

        // 计算校验和
        String checksum = calcSha256(file.getBytes());

        FileResource fr = new FileResource()
                .setName(fname)
                .setOriginalName(originalName)
                .setExt(ext)
                .setSize(file.getSize())
                .setMimeType(mime)
                .setStorageType("local")
                .setPath(target.toString())
                .setChecksum(checksum)
                .setStatus(1)
                .setDeleted(0);
        this.save(fr);
        fr.setPublicId(UUID.randomUUID().toString());
        fr.setUrl("/api/storage/files/" + fr.getPublicId() + "/download");
        this.updateById(fr);
        return fr;
    }

    /**
     * 以字节流方式上传并入库
     * @param content 文件内容
     * @param filename 原始文件名
     * @param mime MIME 类型
     * @return 保存后的实体
     */
    public FileResource uploadLocal(byte[] content, String filename, String mime) throws IOException {
        String ext = (filename != null && filename.contains(".")) ? filename.substring(filename.lastIndexOf('.') + 1) : "";

        Path root = Paths.get(basePath).toAbsolutePath();
        Files.createDirectories(root);
        String fname = System.currentTimeMillis() + "_" + (filename == null ? "file" : filename);
        Path target = root.resolve(fname);
        Files.write(target, content);

        String checksum = calcSha256(content);

        FileResource fr = new FileResource()
                .setName(fname)
                .setOriginalName(filename)
                .setExt(ext)
                .setSize((long) content.length)
                .setMimeType(mime)
                .setStorageType("local")
                .setPath(target.toString())
                .setChecksum(checksum)
                .setStatus(1)
                .setDeleted(0);
        this.save(fr);
        fr.setPublicId(UUID.randomUUID().toString());
        fr.setUrl("/api/storage/files/" + fr.getPublicId() + "/download");
        this.updateById(fr);
        return fr;
    }

    /**
     * DTO 转换
     * @param fr 实体
     * @return DTO
     */
    public FileDTO toDTO(FileResource fr) {
        FileDTO dto = new FileDTO();
        BeanUtils.copyProperties(fr, dto);
        if (fr.getCreateTime() != null) {
            dto.setCreateTime(fr.getCreateTime().toString());
        }
        return dto;
    }

    private static String calcSha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(bytes);
            return HexFormat.of().formatHex(out);
        } catch (Exception e) {
            return "";
        }
    }
}

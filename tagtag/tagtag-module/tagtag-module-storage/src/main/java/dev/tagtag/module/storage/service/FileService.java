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
import dev.tagtag.module.storage.convert.FileMapperConvert;
import dev.tagtag.module.storage.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.util.HexFormat;

/**
 * 文件服务，封装分页、上传、删除等核心逻辑
 */
@Service
@RequiredArgsConstructor
public class FileService extends ServiceImpl<FileMapper, FileResource> {

    @Value("${storage.local.base-path:uploads}")
    private String basePath;
    private final FileMapperConvert fileMapperConvert;

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
     * 上传文件到本地并入库（流式计算校验和）
     * @param file MultipartFile
     * @return 保存后的实体
     */
    public FileResource uploadLocal(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String mime = file.getContentType();
        return saveLocal(file.getInputStream(), file.getSize(), originalName, mime);
    }

    /**
     * 以字节流方式上传并入库
     * @param content 文件内容
     * @param filename 原始文件名
     * @param mime MIME 类型
     * @return 保存后的实体
     */
    public FileResource uploadLocal(byte[] content, String filename, String mime) throws IOException {
        try (InputStream in = new java.io.ByteArrayInputStream(content)) {
            return saveLocal(in, content.length, filename, mime);
        }
    }

    /**
     * DTO 转换
     * @param fr 实体
     * @return DTO
     */
    public FileDTO toDTO(FileResource fr) {
        FileDTO dto = fileMapperConvert.toDTO(fr);
        if (fr != null && fr.getCreateTime() != null) {
            dto.setCreateTime(fr.getCreateTime().toString());
        }
        return dto;
    }

    /**
     * 计算输入字节的 SHA-256 摘要
     * @param bytes 输入字节数组
     * @return 十六进制摘要字符串
     */
    private FileResource saveLocal(InputStream in, long size, String filename, String mime) throws IOException {
        String originalName = filename;
        String ext = (originalName != null && originalName.contains(".")) ? originalName.substring(originalName.lastIndexOf('.') + 1) : "";
        Path root = Paths.get(basePath).toAbsolutePath();
        Files.createDirectories(root);
        String fname = System.currentTimeMillis() + "_" + (originalName == null ? "file" : originalName);
        Path target = root.resolve(fname);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new IOException(e);
        }
        try (DigestInputStream dis = new DigestInputStream(in, md); OutputStream out = Files.newOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = dis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        String checksum = HexFormat.of().formatHex(md.digest());
        String publicId = UUID.randomUUID().toString();
        FileResource fr = new FileResource()
                .setPublicId(publicId)
                .setName(fname)
                .setOriginalName(originalName)
                .setExt(ext)
                .setSize(size)
                .setMimeType(mime)
                .setStorageType("local")
                .setPath(target.toString())
                .setChecksum(checksum)
                .setUrl("/api/storage/files/view/" + publicId)
                .setStatus(1)
                .setDeleted(0);
        this.save(fr);
        return fr;
    }
}

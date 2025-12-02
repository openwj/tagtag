package dev.tagtag.contract.storage.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件上传结果
 */
@Data
@Accessors(chain = true)
public class FileUploadResult {
    private Long id;
    private String url;
    private String name;
    private String originalName;
    private Long size;
    private String mimeType;
}


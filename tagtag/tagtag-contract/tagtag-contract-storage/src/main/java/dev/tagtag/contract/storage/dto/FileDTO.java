package dev.tagtag.contract.storage.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件信息传输对象
 */
@Data
@Accessors(chain = true)
public class FileDTO {
    private Long id;
    private String publicId;
    private String name;
    private String originalName;
    private String ext;
    private Long size;
    private String mimeType;
    private String storageType;
    private String url;
    private String path;
    private String checksum;
    private String bucket;
    private Integer status;
    private String createTime;
}

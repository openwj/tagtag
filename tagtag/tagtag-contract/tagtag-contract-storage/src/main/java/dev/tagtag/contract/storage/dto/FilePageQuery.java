package dev.tagtag.contract.storage.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件列表查询条件
 */
@Data
@Accessors(chain = true)
public class FilePageQuery {
    private String name;
    private String mimeType;
    private String ext;
    private String storageType;
    private String createTimeStart;
    private String createTimeEnd;
}


package dev.tagtag.module.storage.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 存储文件实体（映射 storage_file）
 */
@Data
@Accessors(chain = true)
@TableName("storage_file")
public class FileResource {
    @TableId
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
    private Long createBy;
    private Long updateBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

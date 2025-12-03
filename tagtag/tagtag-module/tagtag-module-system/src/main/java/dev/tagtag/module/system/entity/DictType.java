package dev.tagtag.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型表
 */
@Data
@TableName("sys_dict_type")
public class DictType {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private Long createBy;
    private LocalDateTime updateTime;
    private Long updateBy;
    private Integer deleted;
}

package dev.tagtag.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class Message {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String type;
    private Long senderId;
    private Long receiverId;
    private Integer isRead;
    private LocalDateTime createTime;
    private Long createBy;
    private LocalDateTime updateTime;
    private Long updateBy;
    private Integer deleted;
}

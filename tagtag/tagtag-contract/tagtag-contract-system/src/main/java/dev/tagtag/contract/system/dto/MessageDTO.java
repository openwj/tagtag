package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String title;
    private String content; // 前端对应 message
    private String type;
    private Long senderId;
    private Long receiverId;
    private Boolean isRead;
    private String createTime; // 格式化后的时间字符串，前端对应 date
    private String avatar; // 可选，发送者头像
}

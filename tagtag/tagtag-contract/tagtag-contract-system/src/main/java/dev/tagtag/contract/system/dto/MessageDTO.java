package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String title;
    private String content; 
    private String type;
    private Long senderId;
    private String senderName; // 发送者名称
    private Long receiverId;
    private String receiverName; // 接收者名称
    private Boolean isRead;
    private String createTime; // 格式化后的时间字符串
    private String avatar; // 可选，发送者头像
}

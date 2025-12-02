-- 系统消息表
CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type VARCHAR(50) DEFAULT 'info' COMMENT '消息类型(notification, message, todo)',
    sender_id BIGINT DEFAULT 0 COMMENT '发送者ID(0代表系统)',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by BIGINT DEFAULT 0 COMMENT '创建人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by BIGINT DEFAULT 0 COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息表';

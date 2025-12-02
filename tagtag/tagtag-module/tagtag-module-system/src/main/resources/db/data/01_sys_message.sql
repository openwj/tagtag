-- 插入一些测试数据
INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '欢迎使用系统', '这是您的第一条系统通知。', 'notification', 0, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '欢迎使用系统' AND receiver_id = 1);

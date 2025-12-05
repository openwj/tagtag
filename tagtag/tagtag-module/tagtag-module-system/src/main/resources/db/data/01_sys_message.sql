-- 插入一些测试数据
INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '欢迎使用系统', '尊敬的用户，欢迎您加入我们的大家庭！这是一个全功能的后台管理系统，希望能为您提供便捷的服务。如果您在使用过程中遇到任何问题，请随时联系管理员。', 'notification', 0, 1, 0, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 5 DAY)
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '欢迎使用系统' AND receiver_id = 1);

INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '系统维护通知', '我们将于本周日凌晨 02:00 - 04:00 进行系统升级维护，届时服务可能会出现短暂中断，请提前做好数据保存。给您带来的不便敬请谅解。', 'notification', 0, 1, 0, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 DAY)
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '系统维护通知' AND receiver_id = 1);

INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '待办任务提醒', '您有一个新的待办任务：【审核2023年度财务报表】，请尽快处理。截止日期：2023-12-31。', 'todo', 0, 1, 0, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY)
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '待办任务提醒' AND receiver_id = 1);

INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '安全警告', '系统检测到您的账号在异地登录（IP: 192.168.1.100），如果是您本人操作，请忽略此消息。如果不是您本人操作，请立即修改密码。', 'security', 0, 1, 1, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3 HOUR)
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '安全警告' AND receiver_id = 1);

INSERT INTO sys_message (title, content, type, sender_id, receiver_id, is_read, create_time) 
SELECT '会议通知', '关于下季度产品规划的内部研讨会将于明日上午 10:00 在第一会议室举行，请准时参加。', 'notification', 1, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM sys_message WHERE title = '会议通知' AND receiver_id = 1);

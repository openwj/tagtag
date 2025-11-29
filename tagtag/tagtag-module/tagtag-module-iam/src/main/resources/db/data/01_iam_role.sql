-- 管理员角色
INSERT INTO iam_role (code, name, status, role_type, sort, remark, create_time, update_time, create_by, update_by)
SELECT 'ADMIN', '管理员', 1, 1, 0, '管理员角色', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_role WHERE code='ADMIN');

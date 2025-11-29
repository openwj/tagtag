-- 基础部门
INSERT INTO iam_dept (name, code, parent_id, sort, status, leader, phone, email, remark, create_time, update_time, create_by, update_by)
SELECT '总部', 'ROOT', 0, 0, 1, 'admin', '18800000000', 'root@localhost', '根部门', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_dept WHERE code='ROOT');

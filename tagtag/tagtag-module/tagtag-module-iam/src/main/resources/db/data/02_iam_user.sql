-- 管理员用户
INSERT INTO iam_user (username, password, nickname, email, phone, gender, dept_id, status, is_admin, avatar, remark, employee_no, position, entry_date, password_updated_at, create_by, update_by)
SELECT 'admin', '$2a$10$KepS5QBz3qDvsqAO8yB88eISs/4hcTDQq3xeTF.eI4q.sFkM4xNMK', '管理员', 'admin@localhost', '18800000000', 0,
       (SELECT id FROM iam_dept WHERE code='ROOT'), 1, 1, '/assets/avatar/admin.png', '系统管理员', 'A0001', '系统管理员', CURRENT_DATE, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_user WHERE username='admin');

-- 基础数据（幂等）

-- 管理员用户（默认密码：password）
INSERT INTO iam_user (username, password, nickname, status)
SELECT 'admin', '{bcrypt}$2a$10$QwZFYsCblPhgBHXBgmTdPeJ9IWAb25jOuscU6pvt6dFSmX2rTtO/O', '管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM iam_user WHERE username='admin');

-- 管理员角色
INSERT INTO iam_role (code, name, status)
SELECT 'ADMIN', '管理员', 1
WHERE NOT EXISTS (SELECT 1 FROM iam_role WHERE code='ADMIN');

-- 按钮型菜单（作为后端权限码）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT 0, '创建用户', 'user:create', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:create');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT 0, '更新用户', 'user:update', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:update');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT 0, '删除用户', 'user:delete', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:delete');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT 0, '分配角色', 'user:assignRole', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:assignRole');

-- 绑定 admin 与 ADMIN 角色
INSERT INTO iam_user_role (user_id, role_id)
SELECT u.id, r.id FROM iam_user u, iam_role r
WHERE u.username='admin' AND r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_role ur WHERE ur.user_id=u.id AND ur.role_id=r.id
  );

-- 为 ADMIN 绑定按钮权限
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='user:create'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='user:update'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='user:delete'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='user:assignRole'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
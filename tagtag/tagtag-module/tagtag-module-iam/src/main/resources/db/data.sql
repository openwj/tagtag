-- 管理员用户
INSERT INTO iam_user (username, password, nickname, status, is_admin)
SELECT 'admin', '{bcrypt}$2a$10$QwZFYsCblPhgBHXBgmTdPeJ9IWAb25jOuscU6pvt6dFSmX2rTtO/O', '管理员', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM iam_user WHERE username='admin');

-- 管理员角色（显式包含统一字段：role_type/sort/remark）
INSERT INTO iam_role (code, name, status, role_type, sort, remark)
SELECT 'ADMIN', '管理员', 1, 1, 0, '管理员角色'
WHERE NOT EXISTS (SELECT 1 FROM iam_role WHERE code='ADMIN');

-- 绑定 admin 与 ADMIN 角色
INSERT INTO iam_user_role (user_id, role_id)
SELECT u.id, r.id FROM iam_user u, iam_role r
WHERE u.username='admin' AND r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_role ur WHERE ur.user_id=u.id AND ur.role_id=r.id
  );

-- 功能目录与页面
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT 0, 'IAM', 'iam', '/iam', NULL, 'lucide:users', 2, 1, 0, 0, 0, NULL, '身份与访问管理', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT p.id, '用户管理', 'iam:user', '/iam/user', 'modules/iam/user/index', 'lucide:user', 1, 1, 1, 0, 0, NULL, '用户管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT p.id, '角色管理', 'iam:role', '/iam/role', 'modules/iam/role/index', 'lucide:shield-question', 2, 1, 1, 0, 0, NULL, '角色管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:role');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT p.id, '部门管理', 'iam:dept', '/iam/dept', 'modules/iam/dept/index', 'lucide:shield', 3, 1, 1, 0, 0, NULL, '部门管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT p.id, '菜单管理', 'iam:menu', '/iam/menu', 'modules/iam/menu/index', 'lucide:menu', 4, 1, 1, 0, 0, NULL, '菜单管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:menu');

-- 用户按钮（挂载到 iam:user）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '创建用户', 'user:create', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '更新用户', 'user:update', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '删除用户', 'user:delete', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '分配角色', 'user:assignRole', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:assignRole')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '读取用户', 'user:read', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');

-- 管理员绑定用户权限
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
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='user:read'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );

-- 部门权限按钮
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '创建部门', 'dept:create', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '更新部门', 'dept:update', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '删除部门', 'dept:delete', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '读取部门', 'dept:read', 2, 1, 0, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');

-- 管理员绑定部门权限
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='dept:create'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='dept:update'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='dept:delete'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id FROM iam_role r, iam_menu m
WHERE r.code='ADMIN' AND m.menu_code='dept:read'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id=r.id AND rm.menu_id=m.id
  );


INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色创建', 'role:create', 2, 1, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:create');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色更新', 'role:update', 2, 1, 2, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:update');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色删除', 'role:delete', 2, 1, 3, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:delete');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '分配菜单', 'role:assignMenu', 2, 1, 4, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:assignMenu');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色读取', 'role:read', 2, 1, 5, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:read');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单创建', 'menu:create', 2, 1, 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:create');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单更新', 'menu:update', 2, 1, 2, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:update');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单删除', 'menu:delete', 2, 1, 3, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:delete');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单读取', 'menu:read', 2, 1, 4, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:read');

INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.deleted = 0
WHERE r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

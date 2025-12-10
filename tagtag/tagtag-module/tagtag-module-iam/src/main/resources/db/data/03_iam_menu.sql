-- 功能目录与页面
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT 0, 'IAM', 'iam', '/iam', NULL, 'lucide:users', 10, 1, 0, 0, NULL, '身份与访问管理', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT p.id, '用户管理', 'iam:user', '/iam/user', 'modules/iam/user/index', 'lucide:user', 10, 1, 1, 0, NULL, '用户管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT p.id, '角色管理', 'iam:role', '/iam/role', 'modules/iam/role/index', 'lucide:shield-question', 20, 1, 1, 0, NULL, '角色管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:role');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT p.id, '部门管理', 'iam:dept', '/iam/dept', 'modules/iam/dept/index', 'lucide:shield', 30, 1, 1, 0, NULL, '部门管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT p.id, '菜单管理', 'iam:menu', '/iam/menu', 'modules/iam/menu/index', 'lucide:menu', 40, 1, 1, 0, NULL, '菜单管理页面', CURRENT_TIMESTAMP
FROM iam_menu p WHERE p.menu_code='iam'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:menu');

-- 用户按钮（挂载到 iam:user）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '创建用户', 'user:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '更新用户', 'user:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '删除用户', 'user:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '分配角色', 'user:assignRole', 2, 1, 50, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:assignRole')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:user'), '读取用户', 'user:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='user:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:user');

-- 部门权限按钮（挂载到 iam:dept）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '创建部门', 'dept:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '更新部门', 'dept:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '删除部门', 'dept:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:dept'), '读取部门', 'dept:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dept:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='iam:dept');

-- 角色权限按钮（挂载到 iam:role）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色创建', 'role:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:create');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色更新', 'role:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:update');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色删除', 'role:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:delete');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '分配菜单', 'role:assignMenu', 2, 1, 50, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:assignMenu');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:role'), '角色读取', 'role:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='role:read');

-- 菜单权限按钮（挂载到 iam:menu）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单创建', 'menu:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:create');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单更新', 'menu:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:update');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单删除', 'menu:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:delete');
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='iam:menu'), '菜单读取', 'menu:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='menu:read');

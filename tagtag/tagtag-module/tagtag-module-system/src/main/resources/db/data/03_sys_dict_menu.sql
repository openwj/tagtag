-- 字典管理页面 (挂载到系统管理下)
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, hide_in_menu, link, remark, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system'), '字典管理', 'dict:list', '/system/dict/list', 'modules/system/dict/index', 'lucide:book', 20, 1, 1, 0, NULL, '字典管理页面', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');


-- 补齐后端业务码：dictType:create/update/delete
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典类型创建', 'dictType:create', 2, 1, 11, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictType:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典类型更新', 'dictType:update', 2, 1, 21, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictType:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典类型删除', 'dictType:delete', 2, 1, 31, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictType:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

-- 字典类型读取权限（补齐后端业务码：dictType:read）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典类型读取', 'dictType:read', 2, 1, 5, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictType:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');


-- 补齐后端业务码：dictData:create/update/delete
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典数据创建', 'dictData:create', 2, 1, 41, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictData:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典数据更新', 'dictData:update', 2, 1, 51, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictData:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典数据删除', 'dictData:delete', 2, 1, 61, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictData:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

-- 字典数据读取权限（补齐后端业务码：dictData:read）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, keep_alive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '字典数据读取', 'dictData:read', 2, 1, 6, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dictData:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('dict:list')
WHERE r.code='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id);

-- 追加将读取业务权限授权给 ADMIN 角色
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('dictType:read', 'dictData:read')
WHERE r.code='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id);

-- 追加将新增/更新/删除的业务权限授权给 ADMIN 角色
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('dictType:create', 'dictType:update', 'dictType:delete', 'dictData:create', 'dictData:update', 'dictData:delete')
WHERE r.code='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id);

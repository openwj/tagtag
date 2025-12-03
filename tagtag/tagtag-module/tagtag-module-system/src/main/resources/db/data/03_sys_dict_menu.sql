-- 字典管理页面 (挂载到系统管理下)
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system'), '字典管理', 'dict:list', '/system/dict/list', 'modules/system/dict/index', 'lucide:book', 20, 1, 1, 0, 0, NULL, '字典管理页面', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

-- 字典类型按钮权限
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '新增字典类型', 'dict:type:add', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:type:add')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '修改字典类型', 'dict:type:edit', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:type:edit')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '删除字典类型', 'dict:type:delete', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:type:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

-- 字典数据按钮权限
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '新增字典数据', 'dict:data:add', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:data:add')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '修改字典数据', 'dict:data:edit', 2, 1, 50, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:data:edit')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='dict:list'), '删除字典数据', 'dict:data:delete', 2, 1, 60, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:data:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='dict:list');

-- 赋予 ADMIN 角色权限
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('dict:list', 'dict:type:add', 'dict:type:edit', 'dict:type:delete', 'dict:data:add', 'dict:data:edit', 'dict:data:delete')
WHERE r.code='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id);

-- 系统管理目录
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT 0, '系统管理', 'system', '/system', NULL, 'lucide:settings', 90, 1, 0, 0, 0, NULL, '系统管理目录', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system');

-- 消息中心页面 (挂载到系统管理下)
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, create_time)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system'), '消息中心', 'message:list', '/system/message/list', 'modules/system/message/index', 'lucide:bell', 10, 1, 1, 0, 0, NULL, '消息中心页面', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='message:list');

-- 消息按钮权限
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='message:list'), '读取消息', 'message:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='message:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='message:list');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='message:list'), '删除消息', 'message:delete', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='message:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='message:list');

-- 赋予 ADMIN 角色权限
INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('system', 'message:list', 'message:read', 'message:delete')
WHERE r.code='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id);

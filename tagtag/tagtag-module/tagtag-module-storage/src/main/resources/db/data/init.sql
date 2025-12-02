
-- 存储管理目录（若不存在则创建）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, is_keepalive, create_time, create_by, update_by)
SELECT 0, '存储管理', 'storage', '/storage', NULL, 'lucide:database', 55, 1, 0, 0, 0, NULL, '存储管理目录', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage');


-- 文件管理页面（挂载到 storage 目录）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, is_keepalive, create_time, create_by, update_by)
SELECT p.id, '文件管理', 'storage:file', '/storage/file', 'modules/storage/file/index', 'lucide:folder', 60, 1, 1, 0, 0, NULL, '文件管理页面', 0, CURRENT_TIMESTAMP, 0, 0
FROM iam_menu p WHERE p.menu_code='storage'
  AND NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

-- 文件管理按钮权限（挂载到 storage:file）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='storage:file'), '读取文件', 'file:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='storage:file'), '上传文件', 'file:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='storage:file'), '更新文件', 'file:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='storage:file'), '删除文件', 'file:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='storage:file'), '下载文件', 'file:download', 2, 1, 50, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:download')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='storage:file');

INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('storage','storage:file','file:read','file:create','file:update','file:delete','file:download')
WHERE r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

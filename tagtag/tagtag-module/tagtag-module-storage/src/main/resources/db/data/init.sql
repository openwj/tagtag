
-- 文件管理页面（挂载到系统管理目录，system）
-- 先尝试查找 system 的 ID，如果不存在（如 system 模块未初始化），则暂时挂载到根目录或创建一个 system
INSERT INTO iam_menu (parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type, is_hidden, is_external, external_url, remark, is_keepalive, create_time, create_by, update_by)
SELECT 
    IFNULL((SELECT id FROM iam_menu WHERE menu_code='system'), 0), 
    '文件管理', 'system:file', '/system/file', 'modules/storage/file/index', 'lucide:folder', 60, 1, 1, 0, 0, NULL, '文件管理页面', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

-- 文件管理按钮权限（挂载到 system:file）
INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system:file'), '读取文件', 'file:read', 2, 1, 10, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:read')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system:file'), '上传文件', 'file:create', 2, 1, 20, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:create')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system:file'), '更新文件', 'file:update', 2, 1, 30, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:update')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system:file'), '删除文件', 'file:delete', 2, 1, 40, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:delete')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

INSERT INTO iam_menu (parent_id, menu_name, menu_code, menu_type, status, sort, remark, is_keepalive, create_time, create_by, update_by)
SELECT (SELECT id FROM iam_menu WHERE menu_code='system:file'), '下载文件', 'file:download', 2, 1, 50, '按钮权限', 0, CURRENT_TIMESTAMP, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='file:download')
  AND EXISTS (SELECT 1 FROM iam_menu WHERE menu_code='system:file');

INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.menu_code IN ('system','system:file','file:read','file:create','file:update','file:delete','file:download')
WHERE r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

INSERT INTO iam_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM iam_role r
JOIN iam_menu m ON m.deleted = 0
WHERE r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_role_menu rm WHERE rm.role_id = r.id AND rm.menu_id = m.id
  );

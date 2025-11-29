INSERT INTO iam_user_role (user_id, role_id)
SELECT u.id, r.id FROM iam_user u, iam_role r
WHERE u.username='admin' AND r.code='ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM iam_user_role ur WHERE ur.user_id=u.id AND ur.role_id=r.id
  );

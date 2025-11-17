## 目标
- 将 `V2__seed.sql` 中 admin 初始密码更新为正确的 BCrypt 哈希，避免后续环境登录失败。

## 变更内容
- 文件：`tagtag-module/tagtag-module-iam/src/main/resources/db/migration/iam/V2__seed.sql`
- 将 admin 插入语句的 `password` 值改为有效的 60 字符 BCrypt 哈希（对应明文 `password`）。

## 注意与验证
- 现库已在 v2 版本：要让变更立即生效，需执行一次手工更新：
  - `UPDATE iam_user SET password='<BCrypt哈希>' WHERE username='admin';`
- 后续新环境或清库重建时，将自动应用正确哈希。

## 验证
- 修改后，调用 `POST /api/auth/login` 使用 `admin/password`，应返回令牌。

我将立即更新 V2 脚本并给出同步的数据库 UPDATE 语句用于当前库。
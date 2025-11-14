## 变更内容
- 更新 `tagtag-module-iam/src/main/resources/db/migration/iam/V1__init.sql`，使表结构与实体一致。
- 添加缺失表与字段，统一列命名为下划线风格，保留 `password` 列以兼容现有种子数据。

## 表结构目标
- `iam_user`：id, username, nickname, email, mobile, gender, status, create_time, update_time, create_by, update_by, password；`create_time` 默认当前时间
- `iam_role`：id, code, name, status, create_time, update_time, create_by, update_by
- `iam_permission`：id, code, name, description, create_time, update_time, create_by, update_by
- `iam_dept`：id, name, parent_id, sort, status, create_time, update_time, create_by, update_by；索引 `idx_parent(parent_id)`
- 关联表保持：`iam_user_role`、`iam_role_permission`

## 验证
- 清理或使用空库后启动应用，Flyway 执行 V1/V2；接口联调登录与受保护接口行为（200/401/403）。
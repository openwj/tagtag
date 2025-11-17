## 问题定位
- Flyway 报错：V1（init）迁移验证失败，提示存在半完成改动与历史不一致。
- 关键语法问题：当前 `V1__init.sql` 中 `iam_menu` 的列定义写成了 `menu_type TINYNT(1)`，应为 `TINYINT(1)`。该语法错误会导致 V1 执行失败并记录失败历史。

## 修复方案
1. 修正 SQL 语法
- 更新 `V1__init.sql`：将 `menu_type TINYNT(1)` 更正为 `menu_type TINYINT(1)`，并复核其他列/注释无拼写问题。

2. 清理库状态（开发环境）
- 任选其一：
  - 方式 A（推荐）：重建库
    - `DROP DATABASE <db>; CREATE DATABASE <db> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
  - 方式 B：清理历史与半成品
    - `DROP TABLE IF EXISTS flyway_schema_history;`
    - `DROP TABLE IF EXISTS iam_user, iam_role, iam_menu, iam_dept, iam_user_role, iam_role_menu;`

3. 重新构建与启动
- 打包：`mvn -DskipTests -pl tagtag-start -am package`
- 启动：`java -jar tagtag-start/target/tagtag-start-1.0.0.jar`
- 预期：Flyway按顺序执行修复后的 `V1 → V2`，应用成功启动。

## 配置确认
- 保留：`spring.flyway.enabled: true`、`spring.flyway.locations: classpath:db/migration`、`spring.flyway.baseline-on-migrate: false`、`spring.flyway.clean-disabled: false`
- 移除：`spring.flyway.errorOverrides`、`spring.flyway.clean-on-validation-error`（已弃用）

## 验证
- 登录：`admin / password` 获取令牌。
- 菜单与角色接口：`/iam/menus` 与 `/iam/roles/{id}/menus` 正常。
- 授权：`@PreAuthorize("hasAuthority('PERM_user:create')")` 命中返回 200。

我将先修正 `V1__init.sql` 的 `TINYINT` 拼写，然后按你选择的库清理方式执行构建与启动验证。
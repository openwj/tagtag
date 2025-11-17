## 修复内容
- 修正脚本：在 `tagtag-module-iam/src/main/resources/db/migration/iam/V1__init.sql` 将所有 `TINYNT(1)` 更正为 `TINYINT(1)`（特别是 `menu_type` 字段）。
- 清理构建产物：`mvn clean -pl tagtag-module-iam,tagtag-start -am` 后重新打包。
- 清理开发库（二选一，推荐重建库）：
  - 方式 A（推荐）：
    - `DROP DATABASE <你的库名>;`
    - `CREATE DATABASE <你的库名> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
  - 方式 B（仅清理历史与半成品）：
    - `DROP TABLE IF EXISTS flyway_schema_history;`
    - `DROP TABLE IF EXISTS iam_user, iam_role, iam_menu, iam_dept, iam_user_role, iam_role_menu;`
- 重新构建并启动：
  - 打包：`mvn -DskipTests -pl tagtag-start -am package`
  - 启动：`java -jar tagtag-start/target/tagtag-start-1.0.0.jar`
- 验证：日志按 `V1 → V2` 执行；登录 `admin / password` 成功；菜单与授权接口正常。

## 配置确认
- 仅保留 OSS 支持的 Flyway 键：
  - `spring.flyway.enabled: true`
  - `spring.flyway.locations: classpath:db/migration`
  - `spring.flyway.baseline-on-migrate: false`
  - `spring.flyway.clean-disabled: false`
- 移除：`spring.flyway.errorOverrides`、`spring.flyway.clean-on-validation-error`（已弃用）

如果确认，我将立即修正脚本、清理并重建工程，然后指导你执行数据库清理并验证启动。
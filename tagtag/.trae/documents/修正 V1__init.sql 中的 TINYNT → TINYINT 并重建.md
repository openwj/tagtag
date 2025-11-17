## 变更内容
- 文件：`tagtag-module/tagtag-module-iam/src/main/resources/db/migration/iam/V1__init.sql`
- 修复：第 50 行的 `menu_type TINYNT(1)` 更正为 `menu_type TINYINT(1)`，确保菜单类型字段类型正确。

## 构建与验证
1. 清理并打包：
- `mvn clean -pl tagtag-module-iam,tagtag-start -am`
- `mvn -DskipTests -pl tagtag-start -am package`
2. 启动应用：
- `java -jar tagtag-start/target/tagtag-start-1.0.0.jar`
3. 观察 Flyway 执行：应按 `V1 → V2` 成功初始化。
4. 登录/权限联调：
- `admin / password` 登录；
- 验证菜单/角色接口与 `PERM_user:create` 授权命中。

我将立即修改该行并执行上述构建步骤。
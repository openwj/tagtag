## 目标
- 使用 Flyway 管理并执行各模块的 SQL 脚本，保证有序、幂等与生产可控。
- 保留“每模块都有自己的 application-*.yml”，但不在模块中设置 `spring.flyway.locations`，避免属性覆盖；位置统一由主应用配置为通配路径聚合加载。

## 实施步骤
1. 依赖
- 在主应用或框架层引入：`spring-boot-starter-flyway`（会自动启用 Flyway）。

2. 配置
- 在主应用 `application-dev.yml`/`application-prod.yml` 设置：
  - `spring.flyway.enabled: true`
  - `spring.flyway.locations: classpath*:db/migration/**`
  - `spring.flyway.baseline-on-migrate: true`
  - `spring.flyway.group: true`
- 各模块的 `application-*.yml` 可保留自身其他配置，但不要设置 `spring.flyway.locations`，以防覆盖主应用。

3. 脚本组织
- 每模块将脚本置于自身目录：
  - IAM：`tagtag-module-iam/src/main/resources/db/migration/iam/V1__init.sql`、`V2__seed.sql`
  - System：`tagtag-module-system/src/main/resources/db/migration/system/V1__init.sql`
- 命名与内容规范：
  - 表名前缀区分模块（如 `iam_*`、`sys_*`）。
  - DDL 使用 `IF NOT EXISTS`；DML 使用 `INSERT IGNORE` 或 `ON DUPLICATE KEY UPDATE`，确保幂等。

4. 验证
- 打包：`mvn -DskipTests -pl tagtag-start -am package`
- 启动：`java -jar tagtag-start/target/tagtag-start-1.0.0.jar`
- 观察 Flyway 迁移日志；接口自测登录与受保护接口（200/401/403）。

## 说明
- Flyway 的版本化机制保证脚本顺序与重复执行行为可控；通配位置确保所有模块脚本被聚合加载；避免了 `spring.sql.init.*` 在多模块下的覆盖风险。

确认后我将添加依赖与配置、创建 IAM 模块的迁移脚本，并进行打包和启动验证。
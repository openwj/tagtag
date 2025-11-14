## 优化目标
- 统一依赖与配置规范，避免“驱动找不到”“YAML DuplicateKey”等启动问题。
- 动态数据源与 Flyway 协同：以 `master` 为主数据源，集中迁移脚本位置，模块各自提供脚本但不覆盖位置。
- 每模块可拥有自己的 `application-*.yml`，同时保证属性不相互覆盖、脚本幂等、启动可快速失败与可观测。

## 依赖与版本
- 添加 `mysql-connector-j`（父 BOM 管理版本或显式写入 8.2+）。
- 使用 `flyway-core`（10.x）而非 `spring-boot-starter-flyway`，避免仓库解析问题。
- 保持 `dynamic-datasource-spring-boot3-starter`（已在框架层依赖）。

## 配置规范（通用）
- 每个 `application-*.yml` 文件“只出现一次顶层 `spring:`”，所有配置归在其下（`datasource.dynamic`、`data.redis`、`flyway`、`jackson` 等）。
- 禁止不同模块重复设置相同的全局键会引起覆盖（例如重复定义 `spring.flyway.locations`）。

## 动态数据源（主应用）
- 统一改为：
```
spring:
  datasource:
    dynamic:
      primary: master
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://localhost:3306/tagtag?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
          username: root
          password: root
```
- 生产：`url/user/password` 改为环境变量占位。
- Hikari 快速失败（dev）：`spring.datasource.hikari.minimum-idle: 1`、`spring.datasource.hikari.initializationFailTimeout: 1`。

## Flyway 迁移策略
- 主应用集中启用与聚合位置：
```
spring:
  flyway:
    enabled: true
    locations: classpath*:db/migration/**
    baseline-on-migrate: true
    group: true
```
- 模块仅提供脚本，不设置 `spring.flyway.locations`，避免覆盖；脚本路径约定：
  - IAM：`db/migration/iam/V1__init.sql`、`V2__seed.sql`
  - System：`db/migration/system/V1__init.sql`...
- 命名规范与幂等：
  - 表前缀区分模块：`iam_*`、`sys_*` 等；
  - DDL 用 `IF NOT EXISTS`；DML 用 `INSERT IGNORE` 或 `ON DUPLICATE KEY UPDATE`。

## 模块级 application-*.yml（你的偏好）
- 模块内 `application-dev.yml`/`application-prod.yml` 仅写本模块业务相关键（例如额外的开关）；不要设置会覆盖主应用的聚合键（如 `spring.flyway.locations`）。
- 如确需模块内启用 Flyway：仅 `spring.flyway.enabled: true`，不写 `locations`，仍由主应用聚合。

## Redis 与其他
- 保持 `spring.data.redis.*` 均在同一 `spring:` 下；按环境使用占位符。
- `logging` 与 `jackson` 按主应用统一；模块不要重复定义顶层 `spring:`。

## 安全与密钥管理
- `jwt.secret` 用环境变量注入，禁用默认弱密钥：`JWT_SECRET`。
- 可在 dev 增加 `ApplicationRunner` 做一次 DB/Redis 连接探测并输出日志（仅开发）。

## 可观测性与健康检查
- 启用 Actuator：`management.endpoints.web.exposure.include=health,info`；`management.endpoint.health.show-details=always`。
- 启动后 `GET /actuator/health` 检查 `db` 与 `redis` 状态。

## 测试与验收
- 构建：`mvn -DskipTests -pl tagtag-start -am package`；
- 启动：`java -jar tagtag-start/target/tagtag-start-1.0.0.jar`；观察 Flyway 执行日志；
- 接口联调：`POST /api/auth/login`、`GET /iam/users/1`；验证 200/401/403 行为；
- 变更检查：确保任何 YAML 中不重复顶层 `spring:`；确保主数据源与 Flyway 指向同一库。

## 演进方向（可选）
- 如后续有多租户或多主库迁移，新增 `spring.flyway.url/user/password` 专供迁移连接，保持业务连接用动态数据源独立；
- 引入数据库变更流水与审计机制（Flyway schema history 表备份、迁移哈希校验）。

确认该优化方案后，我将：1）统一改造 dev/prod 的数据源为 `dynamic` 结构；2）检查并清理重复的 `spring` 顶层键；3）确保各模块 YAML 不覆盖主应用的 Flyway 位置；4）验证打包与启动日志、接口行为。
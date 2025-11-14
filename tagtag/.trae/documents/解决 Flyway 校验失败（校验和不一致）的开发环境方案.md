## 问题
- 你已修改了 V1 初始化脚本，数据库里 `flyway_schema_history` 仍保存旧的校验和，启动时 Flyway 校验失败。

## 开发环境修复方案（安全且最简）
- 在 `application-dev.yml` 里启用：
  - `spring.flyway.clean-on-validation-error: true`（校验失败时自动清库）
  - `spring.flyway.clean-disabled: false`（允许执行 clean）
- 效果：遇到校验和不一致，Flyway自动执行 `clean` 然后按新的 V1/V2 重建结构与数据，确保与代码一致。
- 注意：仅在 dev 使用；生产环境不要开启这两个配置。

## 备选（不自动清库）
- 自定义 `FlywayMigrationStrategy`（dev profile）在启动时显式 `clean()` 再 `migrate()`；但相较于上面的配置更复杂，收益相同。

## 执行与验证
1. 更新 dev 配置后，重新打包并启动。
2. 观察日志：Flyway 触发 `clean` 后重新执行 V1/V2；Tomcat端口正常开放。
3. 接口联调：`POST /api/auth/login` 与受保护接口（200/401/403）。

确认后我将修改 dev 配置增加上述两个 Flyway 属性，并完成一次打包与启动验证。
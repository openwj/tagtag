## 问题
- Flyway 报错：Unknown prefix for location: `classpath*:db/migration/**`。Flyway不支持 `classpath*:` 与通配符路径。

## 修复方案
1. 修改迁移路径（dev/prod 两个文件）：
   - `spring.flyway.locations: classpath:db/migration`
   - 保留 `enabled: true`、`baseline-on-migrate: true`、`group: true`
   - 说明：Flyway会递归扫描该目录及子目录（例如 `db/migration/iam`、`db/migration/system`），无需通配符。
2. 可选（多根目录显式配置）：
   - 使用逗号分隔多个位置，如：`spring.flyway.locations: classpath:db/migration/iam, classpath:db/migration/system`
   - 推荐统一根：`db/migration`，保持模块脚本位于子目录即可。
3. 数据源联动（保持现状）：
   - 继续使用动态数据源，Flyway默认使用主数据源（`primary: master`）。若需要独立连接，可在 dev 设置：
     - `spring.flyway.url/user/password` 指向 `master`。
4. 验证
   - 重新打包并启动；观察 Flyway 执行 `V1/V2` 日志与 Tomcat端口；
   - 接口联调：`POST /api/auth/login` 与受保护接口 200/401/403。

## 备用措施
- 临时停用迁移：`spring.flyway.enabled=false`（仅用于排障）。

我将按上述方案更新 dev/prod 的 Flyway 路径并验证启动。
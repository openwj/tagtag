## 问题根因
- 开发环境启用了 `spring.flyway.baseline-on-migrate: true`，当库存在历史对象或某些场景下，会在迁移前将基线设为版本 1，从而跳过 `V1__init.sql`。
- 结果：`V2__seed.sql` 执行时找不到 `iam_menu` 表，报错“Table ... iam_menu doesn't exist”。

## 修复方案（dev）
1. 关闭基线自动设定：将 `spring.flyway.baseline-on-migrate` 设为 `false`，确保 `V1` 正常执行。
2. 保留已启用的自动清库：`spring.flyway.clean-on-validation-error: true`、`spring.flyway.clean-disabled: false`，遇到历史不一致自动 clean 并从 `V1` 重建。
3. 可选手动清理（若仍有历史残留）：删除库中的 `flyway_schema_history` 表或直接清库。

## 修改位置
- 文件：`tagtag-start/src/main/resources/application-dev.yml`
- 字段：`spring.flyway.baseline-on-migrate: false`

## 验证
- 重新打包启动，观察日志：执行 `V1__init.sql` → 执行 `V2__seed.sql` → 应用启动成功。
- 接口自测：菜单分页、角色菜单查询与分配、登录与权限命中（200/401/403）。

确认后我将更新 dev 配置并执行打包启动验证。
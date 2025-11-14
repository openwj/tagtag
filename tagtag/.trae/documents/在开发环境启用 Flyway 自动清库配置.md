## 变更内容
- 在 `tagtag-start/src/main/resources/application-dev.yml` 的 `spring.flyway` 下添加：
  - `clean-on-validation-error: true`
  - `clean-disabled: false`
- 仅 dev 生效，prod 不修改。

## 效果
- 当迁移校验和不一致时，Flyway 自动执行 `clean` 后按最新的 V1/V2 重新初始化数据库结构与数据，保证与代码一致。

## 验证
- 重新打包并启动应用，观察日志出现 `clean` 与 `migrate v1/v2`，Tomcat端口正常启动。
- 接口联调认证与受保护接口返回 200/401/403 行为正常。
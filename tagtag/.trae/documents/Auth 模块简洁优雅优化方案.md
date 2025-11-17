## 优化目标
- 充分利用 Spring/Security 内置能力，去除不必要的手写逻辑。
- 提炼重复代码到封装类或配置，统一风格，保证简洁且功能完整。

## 建议与具体改动
1. 使用内置 DelegatingPasswordEncoder
- 位置：`tagtag-framework/src/main/java/dev/tagtag/framework/config/SecurityConfig.java`
- 改动：将 `@Bean PasswordEncoder` 从 `new BCryptPasswordEncoder()` 改为 `PasswordEncoderFactories.createDelegatingPasswordEncoder()`，内置支持 `{bcrypt}`、`{noop}` 前缀。
- 效果：在 `AuthServiceImpl` 去除手动 `{bcrypt}` 剥离与格式判断，直接 `encoder.matches(pwd, stored)` 即可。

2. 外部化令牌 TTL 配置
- 位置：`tagtag-start/src/main/resources/application-*.yml` 与 `AuthServiceImpl`
- 改动：新增 `security.jwt.access-ttl-seconds` 与 `security.jwt.refresh-ttl-seconds` 配置项；`AuthServiceImpl` 用 `@Value` 或 `@ConfigurationProperties` 注入，移除硬编码常量。
- 效果：不同环境可灵活调整有效期，代码更干净。

3. Dev 专用工具端点隔离
- 位置：`AuthController.bcrypt(...)` `tagtag-module/tagtag-module-auth/src/main/java/dev/tagtag/module/auth/controller/AuthController.java:67-72`
- 改动：给该方法加 `@Profile("dev")` 或抽到 `DevToolsController` 并加 `@Profile("dev")`。
- 效果：生产环境不会暴露辅助接口，代码语义更清晰。

4. 常量与文案统一
- 位置：`AuthServiceImpl`
- 改动：将统一文案 `"凭证无效"` 抽到常量（如 `dev.tagtag.kernel.constant.SecurityMessages.INVALID_CREDENTIALS`），统一引用。
- 效果：避免魔法字符串，后续变更集中。

5. Service 层参数校验简化
- 位置：`AuthServiceImpl.login(...)` `tagtag-module/.../AuthServiceImpl.java:46-51`
- 改动：控制器已用 `@Valid @NotBlank` 校验，Service 层可改为“防御性早返回”或直接依赖控制器校验，减少重复判断。
- 效果：职责更清晰，减少重复校验代码。

6. PermissionResolver 继续保持封装
- 位置：`tagtag-module/tagtag-module-auth/src/main/java/dev/tagtag/module/auth/service/PermissionResolver.java`
- 建议：若后续多模块复用权限解析能力，可考虑在 IAM 模块提供“按多角色批量查询菜单”的 API 以减少循环远程调用；当前保持在 `auth` 模块即可。

7. 轻量日志与测试
- 在关键失败分支添加轻量 `@Slf4j` 调试日志（不暴露敏感），并补充 MockMvc 测试用例覆盖登录成功/失败、注销后旧令牌 401。

## 交付清单
- 替换 `PasswordEncoder` 为 DelegatingPasswordEncoder，并简化 `AuthServiceImpl` 口令匹配逻辑。
- 新增 TTL 配置并在代码中注入使用。
- 给 bcrypt 工具端点加 `@Profile("dev")`（或抽出至 dev 专用控制器）。
- 抽取统一文案常量并替换引用。
- 精简 Service 层参数校验。
- 可选：新增/完善测试用例与少量日志。

## 影响评估
- 登录/刷新/注销行为不变；代码更短更清晰。
- 安全性提升（避免手工密码格式判断）。
- 配置化增强可维护性，生产/开发差异更可控。

我可以按上述方案逐项实现，并在本地编译与运行（dev）验证接口逻辑与版本失效仍正确。
## 目标
- 采用方案 B：使用成熟 JWT 库（推荐 Nimbus JOSE + JWT）生成令牌，并用 Spring Security `oauth2-resource-server` 的 `JwtDecoder` 做验证与认证，统一写入 `SecurityContext`。
- 保留无状态架构与统一 JSON 异常输出；对齐认证端点路径；移除/合并自研与重复过滤器。

## 依赖与配置
1. 在 `pom.xml` 增加：`spring-boot-starter-oauth2-resource-server`、`nimbus-jose-jwt`（仅生成端需要）。
2. 在应用配置中外部化密钥与 TTL：`jwt.secret`、`jwt.access-ttl`、`jwt.refresh-ttl`，后续支持 `kid` 与密钥轮换。

## 安全过滤链调整
1. 修改 `SecurityConfig`：
   - 使用 `http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(customConverter)`；保留 `CustomAuthenticationEntryPoint` 与 `CustomAccessDeniedHandler`（`SecurityConfig.java:31-45`）。
   - 路径白名单改为 `"/auth/**"` 或将控制器改到 `"/api/auth/**"`，二选一确保一致（当前不一致：`SecurityConfig.java:39` vs `AuthController.java:22`）。
2. 移除 `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)` 与框架层自研 `JwtAuthenticationFilter` 的职责，改由资源服务器自动解析并写入 `SecurityContext`。

## 权限映射
1. 编写 `JwtAuthenticationConverter`：从 JWT claims 中读取 `roles` 与 `perms`，映射为 `GrantedAuthority`（如 `ROLE_xxx` 与 `PERM_xxx`）。
2. 允许后续在方法级使用 `@PreAuthorize("hasAuthority('PERM_...')")`（可选）。

## 令牌生成服务替换
1. 新建 `JwtService`（使用 Nimbus）：支持 HS256 签名，生成访问令牌与刷新令牌，保留现有 claims（`uid`、`uname`、`roles`、`perms`、`typ`、并补充 `iat`、`nbf`、`jti`）。
2. 修改 `AuthServiceImpl`：注入 `JwtService` 替代当前 `new JwtProvider(new ObjectMapper())`（`AuthServiceImpl.java:29`），使用配置化 TTL 与密钥生成令牌（保持 `TokenDTO` 返回结构不变）。

## 路径对齐（两案择一，推荐控制器改为带前缀）
- 推荐：将 `AuthController` 的 `@RequestMapping` 改为 `"/api/auth"`（与 `GlobalConstants.API_PREFIX` 一致），并在 `SecurityConfig` 放行 `"/api/auth/**"`；或反向把白名单改为 `"/auth/**"` 并更新常量。

## 清理与统一
- 移除/停用框架与模块内重复的 `JwtAuthenticationFilter`，避免两套并存（`tagtag-framework/.../JwtAuthenticationFilter.java` 与 `tagtag-module-auth/.../JwtAuthenticationFilter.java`）。
- 移除自研 `JwtProvider` 的直接使用；如保留可仅用于工具类或兼容测试。

## 测试与验收
- 单元测试：令牌生成/校验、过期与刷新、必备 claims、时钟漂移。
- 集成测试：
  - 登录/刷新端点白名单可访问；
  - 携带访问令牌访问受保护接口返回 200；
  - 未认证返回 401（`CustomAuthenticationEntryPoint`），越权返回 403（`CustomAccessDeniedHandler`）。
- 安全回归：签名篡改、过期、错误算法、错误受众/发行者。

## 交付内容
- 依赖与配置变更。
- `SecurityConfig` 更新与 `JwtAuthenticationConverter` 实现。
- 新的 `JwtService` 与对 `AuthServiceImpl` 的改造。
- 路径一致性修复与重复过滤器清理。

请确认以上计划（尤其路径对齐方案选用），我将开始按方案 B 修改并验证。
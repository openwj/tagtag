## 原因解释
- 当前使用无状态 JWT（`SecurityConfig.java:44-55`），服务器不维护会话；只要令牌在有效期内且签名通过，就会被接受。
- `AuthServiceImpl.logout(String accessToken)` 为空实现是符合“无状态”设计：服务端无法主动让客户端令牌失效，通常由客户端删除本地令牌完成“退出登录”。
- 若需要服务端“强制失效”，需新增令牌撤销机制（黑名单或版本控制）。

## 方案选型
- 黑名单（推荐）：
  - 使用现有 JWT 的 `jti`（已生成，`JwtService.java:41-43`）作为唯一标识，将被撤销的 `jti` 或完整 token 存入 Redis，设置 TTL 为剩余有效期。
  - 每次鉴权前检查 `jti` 是否在黑名单中，命中则拒绝。
- 令牌版本（可选/扩展）：
  - 在 claims 增加 `ver`，服务端为用户维护当前版本号；当用户退出或禁用时提升版本，旧版本令牌一律拒绝。

## 实施步骤
1. 新增黑名单服务
   - `JwtBlacklistService`：封装 Redis 读写，方法：
     - `void revoke(String jti, long ttlSeconds)`：将 jti 放入黑名单并设置 TTL。
     - `boolean isRevoked(String jti)`：检查是否已撤销。
   - 函数级注释：说明职责与参数。
2. 接入过滤/校验
   - 方案 A：自定义 `OncePerRequestFilter`，在 `SecurityFilterChain` 中添加到 `oauth2ResourceServer().jwt()` 解码之后，读取 `jwt.getId()` 或 claims `jti` 并调用 `isRevoked`，命中则返回 401。
   - 方案 B：自定义 `JwtAuthenticationConverter` 前置检查（也可在 `AuthenticationEntryPoint` 阶段处理）。
   - 推荐 A，职责更清晰。
3. 改造注销逻辑
   - 在 `AuthServiceImpl.logout(String accessToken)`：
     - 解析 `jti` 与过期时间（通过 `SignedJWT` 或现有 `JwtService.getClaims`），计算剩余秒数。
     - 调用 `JwtBlacklistService.revoke(jti, ttlSeconds)`。
   - 如果有刷新令牌，一并撤销（前端传或服务端关联）。
4. 令牌版本（可选增强）
   - `generateToken` 时增加 `ver`；为用户维护 `currentTokenVersion`（存储于 Redis），验证时要求令牌 `ver == currentTokenVersion`。
   - 退出登录或禁用用户时，`currentTokenVersion++`，所有旧令牌即刻失效。
5. 配置与依赖
   - 使用已有 `spring-boot-starter-data-redis`（已在框架模块引入，`tagtag-framework/pom.xml:41-43`）与现有 Redis 连接配置。
   - 仅在生产环境启用黑名单开关（例如 `security.jwt.blacklist.enabled=true`），开发环境可按需。
6. 测试
   - 用例：
     - 正常访问 → 200。
     - 注销后用原访问令牌访问 → 401。
     - 注销后用刷新令牌刷新 → 401（若也撤销）。
     - 黑名单 TTL 到期后令牌再次可用（仅用于验证逻辑，生产不建议依赖）。

## 交付内容
- 新增 `JwtBlacklistService` 与 `JwtRevocationFilter`（或同等命名），以及 `SecurityConfig` 中的过滤器注册。
- 改造 `AuthServiceImpl.logout` 实现令牌撤销。
- 所有新增代码包含函数级注释。

## 验收标准
- 登出后，使用原令牌请求资源返回 401，响应结构由全局异常处理统一。
- 构建与运行通过，集成测试覆盖主要场景。
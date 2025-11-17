## 目标
- 将当前散落在 `AuthServiceImpl` 的通用辅助逻辑封装为可复用的工具类/服务，提升可读性与复用性。
- 保持行为与安全策略不变（错误码、文案、令牌版本）。

## 封装设计
- 公共字符串工具（新增）：`tagtag-common/src/main/java/dev/tagtag/common/util/Strings.java`
  - `String normalize(String s)`：返回 `s == null ? null : s.trim()`
  - `String normalizeToNull(String s)`：`trim` 后若为空返回 `null`
- 密码工具（新增）：`tagtag-framework/src/main/java/dev/tagtag/framework/security/PasswordUtils.java`
  - `String stripBcryptPrefix(String stored)`：移除 `{bcrypt}` 前缀
  - `boolean isBcryptHash(String stored)`：判断是否为 BCrypt 格式（`$2a$/$2b$/$2y$`）
  - `boolean matchesBcrypt(String raw, String hash, PasswordEncoder encoder)`：校验明文与哈希
- 权限解析服务（新增）：`tagtag-module-auth/src/main/java/dev/tagtag/module/auth/service/PermissionResolver.java`
  - `Set<String> resolvePermissions(List<Long> roleIds)`：基于 `RoleApi` 扁平化去重收集 `menuCode`
- 令牌工厂（新增）：`tagtag-module-auth/src/main/java/dev/tagtag/module/auth/service/TokenFactory.java`
  - `Map<String,Object> buildClaims(UserDTO full, List<Long> roleIds, Set<String> perms, long ver)`
  - `TokenDTO issueTokens(Map<String,Object> claims, String subject)`：发放访问/刷新令牌

## 重构调整
- `AuthServiceImpl`：
  - 使用 `Strings.normalize(...)` 处理输入
  - 用 `PasswordUtils.stripBcryptPrefix/isBcryptHash/matchesBcrypt` 校验口令
  - 将权限解析与 claims/令牌发放逻辑委托给 `PermissionResolver` 与 `TokenFactory`
  - 删除原有私有方法 `normalize/normalizeStoredHash/verifyPassword/resolvePermissions/buildClaims/issueTokens`
  - 保留 `Numbers.toLong` 已封装的数值转换

## 验证
- 编译打包通过
- 开发环境运行自测：
  - 登录成功返回令牌；错误分支保持 400/401 与统一“凭证无效”
  - 注销后旧令牌 401（版本过滤生效）

## 交付
- 新增 4 个封装类，重构 `AuthServiceImpl`，所有方法保留函数级注释。
- 控制文件数量与作用域：通用工具放 `common`/`framework`，业务封装留在模块内。

如确认，我将按上述设计直接实现并验证。
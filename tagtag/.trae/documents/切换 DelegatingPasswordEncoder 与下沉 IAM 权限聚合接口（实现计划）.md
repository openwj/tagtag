## 目标
- 使用内置 DelegatingPasswordEncoder，简化口令校验并兼容多算法/带前缀哈希。
- 在 IAM 模块提供“按多角色批量查询菜单编码”的聚合接口，减少循环远程调用并统一权限域逻辑。

## 改动清单
### 1. 安全层密码编码器
- 文件：`tagtag-framework/src/main/java/dev/tagtag/framework/config/SecurityConfig.java`
- 变更：将 `@Bean PasswordEncoder` 改为 `PasswordEncoderFactories.createDelegatingPasswordEncoder()` 并正确导入类。
- 依赖：确认 `spring-boot-starter-security` 已在 `tagtag-framework/pom.xml`；如编译缺类，则显式添加 `spring-security-crypto` 依赖。
- 兼容策略：
  - 新生成哈希带前缀（如 `{bcrypt}`），无需剥离；
  - 旧数据无前缀时，Delegating 的默认匹配器为 bcrypt，可直接 `matches`。

### 2. AuthServiceImpl 口令校验调整
- 文件：`tagtag-module/tagtag-module-auth/.../AuthServiceImpl.java`
- 变更：删除对 `{bcrypt}` 的手工剥离与手工算法分支，统一 `passwordEncoder.matches(pwd, stored)`。
- 保持函数级注释与统一文案 `SecurityMessages.INVALID_CREDENTIALS`。

### 3. IAM 聚合接口
- 新增 Service 与 API 方法：
  - Service：`RoleService.listMenuCodesByRoleIds(List<Long>): Set<String>`
  - API：`RoleApi.listMenuCodesByRoleIds(List<Long>): Result<Set<String>>`
- Mapper SQL（单次查询去重）：`SELECT DISTINCT m.menu_code FROM iam_role_menu rm JOIN iam_menu m ON m.id=rm.menu_id WHERE rm.role_id IN (...) AND m.deleted=0`
- 实现位置：`tagtag-module-iam`（Service/Mapper/Api 实现类），方法添加函数级注释。

### 4. PermissionResolver 改造
- 文件：`tagtag-module/tagtag-module-auth/src/main/java/dev/tagtag/module/auth/service/PermissionResolver.java`
- 变更：由循环调用 `RoleApi.listMenusByRole(rid)` 改为一次调用 `RoleApi.listMenuCodesByRoleIds(roleIds)`，直接返回 `Set<String>`。

### 5. 配置与测试
- 保持已完成的 `security.jwt.*` TTL 配置；
- 新增/完善 MockMvc 测试用例：登录成功/失败、注销后旧令牌 401、聚合接口生效（角色集合对应的权限集合）。

### 6. 验证
- 全项目编译打包；
- 本地以 `dev` profile 运行，验证登录/刷新/注销与权限解析行为一致且性能更优（减少远程循环）。

## 风险与回滚
- 如 Delegating 编译缺少类，按依赖补齐；若仍不稳定，可回退至 `BCryptPasswordEncoder`（保持剥离逻辑兼容旧数据）。
- IAM 聚合接口为新增，不影响既有接口；若有问题，`PermissionResolver` 可临时回退至旧循环调用。

## 交付
- 修改的类与新增类均包含函数级注释；
- 代码优雅简洁，功能保持一致并更易维护；
- 完成构建与基本集成验证。
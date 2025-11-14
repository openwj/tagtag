## 问题
- 合同层（tagtag-contract-iam）仍残留与 Permission 相关的接口与类型，导致实现层改为 Menu 后两侧不一致。

## 改造范围
1. 接口与类型重命名
- 删除/替换 `PermissionApi` 为 `MenuApi`，方法与路由契约对应：
  - `page(MenuQueryDTO, PageQuery): PageResult<MenuDTO>`
  - `getById(Long): MenuDTO`
  - `create(MenuDTO): void`
  - `update(MenuDTO): void`
  - `delete(Long): void`
- `PermissionDTO`/`PermissionQueryDTO` 已改为 `MenuDTO`/`MenuQueryDTO`（确认无遗漏引用）。
- `RoleApi`：统一提供 `listMenusByRole(Long): Result<List<MenuDTO>>`（已改），清理旧 `listPermissionsByRole`。

2. 依赖与包引用
- 修正 contract-iam 中所有遗留的 `Permission*` 引用为 `Menu*`：
  - DTO 包、API 包、请求封装类、可能的 Feign client（如果存在）
- 同步 module-iam 与 module-auth 对新接口的调用，确保编译通过。

3. 代码规范与注释
- 按你的要求在新/改动的接口方法上添加函数级注释（中文），说明用途、参数与返回。

## 验证
- 编译通过：`mvn -DskipTests -pl tagtag-start -am package`
- 启动（dev 自动 clean + 迁移）：`java -jar tagtag-start/target/tagtag-start-1.0.0.jar`
- 接口联调：
  - 菜单分页/详情/增改删契约生效；
  - 角色菜单查询与分配契约生效；
  - 登录令牌聚合按钮型菜单 `menuCode` 成为权限集。

若确认，我将在 contract-iam 中新增 `MenuApi` 并移除 `PermissionApi`，修正所有遗留引用后完成编译与验证。
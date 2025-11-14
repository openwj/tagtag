## 方案概览
- 将权限表 `iam_permission` 迁移为菜单/资源表 `iam_menu`（承载目录/菜单/按钮），以按钮的 `menu_code` 表示后端权限码，适配前后端分离的路由与资源控制。
- 角色关联从 `iam_role_permission` 迁移为 `iam_role_menu`；登录发令牌时根据角色绑定的按钮型菜单生成权限集合。
- 仅在开发环境变更（已启用 Flyway 自动 clean），安全快速落地。

## 数据库变更（V1 直改，开发环境）
1. 在 `V1__init.sql` 中：
- 删除 `iam_permission`、`iam_role_permission`；新增 `iam_menu` 与 `iam_role_menu`。
- `iam_menu` 参考你提供的 `sys_menu` 结构，字段包含：
  - id, parent_id, menu_name, menu_code, path, component, icon, sort, status, menu_type(0/1/2), is_hidden, is_external, external_url, remark, is_keepalive, create_time, create_by, update_time, update_by, deleted
  - 唯一索引 `uk_menu_code`；父级、名称、状态、排序、类型、隐藏、外链、创建时间等索引
- `iam_role_menu`：`role_id`、`menu_id` 联合唯一、索引（与 `iam_user_role` 命名和结构保持一致风格）
2. 在 `V2__seed.sql` 中：
- 插入基础菜单数据（目录/菜单/按钮），按钮型 `menu_type=2`，`menu_code` 即权限码
- 绑定 `ADMIN` 角色与按钮型菜单的关系

## 代码改造
1. 实体与 Mapper（IAM 模块）
- 新增 `Menu` 实体（映射 `iam_menu`），替换 `Permission` 相关：
  - `dev.tagtag.module.iam.entity.Menu`（字段与表一致）
  - `MenuMapper`（替换 `PermissionMapper`，保留分页/条件查询接口）
- 关联 Mapper：新增/替换角色关联查询方法（按角色查询菜单、只取 `menu_type=2` 的按钮）
2. 契约层（tagtag-contract-iam）
- 新增 `MenuDTO`；
- 在 `RoleApi` 中将 `listPermissionsByRole(rid)` 替换为 `listMenusByRole(rid)`，或保留旧名但返回按钮型菜单以维持向后兼容（推荐同时提供新方法并在实现中桥接）
3. 服务层与控制器（IAM 模块）
- `RoleService`/`RoleController`：增加基于菜单的查询与分配接口（分配菜单/按钮权限）；
- 用户菜单树接口（前端路由）：提供按用户聚合的目录/菜单结构，按钮集合作为操作权限
4. 认证模块（AuthServiceImpl）
- 聚合权限时，改为读取角色绑定的按钮型 `iam_menu.menu_code`：
  - `perms = {menu_code | menu_type=2}`
  - 令牌 claims 中 `perms` 不变，继续映射为 `PERM_<code>`（`SecurityConfig` 的 `JwtAuthenticationConverter` 已兼容）
5. 清理与兼容
- 删除/废弃 `Permission` 相关类与接口；如需短期兼容，保留旧接口但由菜单实现返回按钮集合

## 安全授权与前端适配
- 前端根据菜单表驱动路由与可见性；按钮型 `menu_code` 用于后端方法级授权（`@PreAuthorize('hasAuthority("PERM_xxx")')`）与前端按钮显隐。
- 目录/菜单（`menu_type=0/1`）用于构建路由树；`is_hidden/is_external/is_keepalive` 贯通前端行为。

## 迁移与验证
- 因为是新项目，直接更新 V1/V2 脚本后：
  - 构建运行（dev 已启用 `clean-on-validation-error=true`），自动清库并按新的 V1/V2 重建
  - 验证：
    - `POST /api/auth/login` 获取令牌；
    - 携带令牌访问受保护接口（按钮权限命中则 200，未授权 403，未认证 401）
    - 拉取用户菜单树与按钮权限，验证前端路由与操作控制

## 交付内容
- 更新 V1/V2 SQL（替换权限为菜单）
- 新增 `Menu` 相关实体/Mapper/DTO/服务与接口
- 改造令牌生成的权限聚合逻辑为按钮型菜单 `menu_code`
- 文档化按钮权限与方法级注解的命名规范（`PERM_<menu_code>`）

确认后我将按上述方案更新 SQL 与代码，并在 dev 环境完成自动清库、迁移与联调验证。
## 目标
- 简化单表查询，统一使用 MyBatis-Plus 的 LambdaQuery/Wrapper；复杂联表与批量操作继续保留 XML。
- 保持分页、排序白名单、安全与功能等价；所有方法添加函数级注释。

## 优化范围与策略
- 用户（IAM/User）：
  - `UserServiceImpl.page(...)` 改为 `lambdaQuery` 构建 WHERE 与 ORDER BY，并使用 `Pages.toPage(pageQuery)`；不再依赖 `UserMapper.xml#selectPage`。
  - `UserServiceImpl.getByUsername(...)` 改为 `lambdaQuery().eq(User::getUsername, uname).last("LIMIT 1").one()`；不再依赖 `UserMapper.xml#selectByUsername`。
  - `selectRoleIdsByUserId/deleteUserRoles/insertUserRoles` 保留 XML（涉及非 User 表与批量操作）。
- 角色（IAM/Role）：
  - `RoleServiceImpl.page(...)` 改为 `lambdaQuery`（like code/name、eq status、orderBy）；不再依赖 `RoleMapper.xml#selectPage`。
  - `selectPermissionIdsByRoleId/insertRolePermissions/deleteRolePermissions/selectPermissionCodesByRoleIds` 保留 XML（联表与批量）。
- 菜单（IAM/Menu）：
  - `MenuServiceImpl.page(...)` 改为 `lambdaQuery`（like code/name、orderBy）；不再依赖 `MenuMapper.xml#selectPage`。
- 部门（IAM/Dept）：
  - 若存在简单分页/条件查询，同样改为 `lambdaQuery`；复杂 SQL 保留 XML。

## 排序与安全
- 继续使用既有 `SortWhitelists.*` 白名单映射；将 `PageQuery.sortFields` 转换为 `orderByAsc/Desc`，避免自由拼接 SQL。
- LIKE/范围条件用 Wrapper 的 `like/between/ge/le`，与 XML 等价。

## 交付内容
- 重构 `UserServiceImpl/RoleServiceImpl/MenuServiceImpl/DeptServiceImpl` 的分页与简单查询为 Wrapper 实现。
- 保留复杂 XML 与批量操作 XML；不删除 XML 文件，服务层逐步停用对应简单 `selectPage/selectByUsername`。
- 所有新方法添加函数级注释，构建通过并完成基本接口自测（分页与条件结果一致）。

## 验证
- 运行 dev profile，用已有前端/接口调用验证分页与查询等价；
- 全项目编译打包成功。

请确认，我将按上述范围开始代码改造与验证。
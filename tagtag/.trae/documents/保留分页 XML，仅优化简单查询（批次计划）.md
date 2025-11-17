## 目标
- 分页查询继续使用 XML；仅对简单的单表查询改为 MyBatis-Plus LambdaQuery/Wrapper，写法更简洁优雅。

## 范围
- IAM/User：
  - 保留 `UserMapper.xml#selectPage`
  - 优化 `UserServiceImpl.getByUsername(String)`：改为 `lambdaQuery().eq(User::getUsername, uname).one()`；不再调用 XML `selectByUsername`
  - 角色关联的批量操作与联表查询（`selectRoleIdsByUserId/deleteUserRoles/insertUserRoles`）继续保留 XML
- IAM/Role：
  - 保留 `RoleMapper.xml#selectPage`
  - 如有简单单条查询（如按 code/name 获取单条），统一用 `lambdaQuery()`（若存在）
- IAM/Menu/Dept：
  - 保留各自的 `selectPage`
  - 如有简单单条/列表查询（如按父 ID 查询列表），统一用 `lambdaQuery()`（若存在）

## 实施步骤
1. User 简单查询优化
- 修改 `UserServiceImpl.getByUsername` 为 LambdaQuery；保留函数级注释
- 选择保留 XML 的 `selectByUsername` 以兼容，但服务层改为不使用它

2. 其它模块巡检
- 检查 `RoleServiceImpl/MenuServiceImpl/DeptServiceImpl` 是否存在简单单表查询入口，统一改为 LambdaQuery；不触碰分页 XML

3. 验证
- 编译打包通过
- 使用 dev 环境快速验证：按用户名登录路径正常；分页接口保持不变

## 交付
- 仅改动服务层简单查询方法；分页 XML 与复杂 XML 不改动；所有新增/修改方法附函数级注释。

确认后我将按上述范围实施并验证。
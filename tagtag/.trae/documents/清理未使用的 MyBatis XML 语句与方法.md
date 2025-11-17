## 目标
- 删除已不再使用的 XML 映射与对应 Mapper 方法，保持代码整洁，避免误用。

## 清理范围
- User 模块：
  - 移除 `UserMapper.xml` 的 `<select id="selectByUsername">` 语句（已在 `UserServiceImpl.getByUsername` 改为 LambdaQuery）
  - 删除 `UserMapper` 接口中的 `User selectByUsername(String username)` 方法声明
- 其它模块（Role/Menu/Dept）：
  - 保留分页查询 `selectPage` XML（仍在使用）
  - 保留联表/批量操作 XML（仍在使用）

## 实施步骤
1. 编辑 `tagtag-module-iam/src/main/resources/mapper/iam/UserMapper.xml`：删除 `selectByUsername` 节点
2. 编辑 `tagtag-module-iam/src/main/java/.../UserMapper.java`：删除 `selectByUsername` 方法声明
3. 编译验证：全项目构建通过

## 验证
- 运行 dev 环境，登录路径按用户名查询仍正常（服务侧用 LambdaQuery）
- 其它分页与批量接口不受影响

确认后我将立即删除以上未使用的 XML 与方法，并完成编译验证。
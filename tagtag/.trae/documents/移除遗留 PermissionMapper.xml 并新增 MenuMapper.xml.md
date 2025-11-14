## 问题
- 启动失败因 MyBatis 仍在加载 `mapper/iam/PermissionMapper.xml`，与我们改造后的 Java 映射不匹配（命名空间和表字段都已变更）。

## 解决方案
1. 删除遗留 XML
- 移除 `tagtag-module-iam/src/main/resources/mapper/iam/PermissionMapper.xml`，避免 MyBatis 解析旧命名空间与表结构。

2. 新增菜单映射 XML
- 新建 `tagtag-module-iam/src/main/resources/mapper/iam/MenuMapper.xml`：
  - `namespace="dev.tagtag.module.iam.mapper.MenuMapper"`
  - `select id="selectPage" resultType="dev.tagtag.module.iam.entity.Menu"`
  - 查询表 `iam_menu`，支持筛选 `q.menuCode`、`q.menuName`；
  - 排序字段映射 `id/menu_code/menu_name/create_time/sort`，与 `SortWhitelists.permission()` 保持一致；
  - 列选择与驼峰映射保持：`create_time AS createTime` 等。

3. 验证
- 打包并启动，确认不再解析旧 XML；
- 自测菜单分页与角色菜单查询接口。

若确认，我将立即执行上述文件删除与新增操作，并进行构建启动验证。
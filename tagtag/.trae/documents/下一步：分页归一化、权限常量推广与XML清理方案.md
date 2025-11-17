## 目标
- 推进“最优解”落地：分页默认值配置化、权限常量全面统一、清理剩余简单 XML 查询，保持架构一致性与可维护性。

## Phase A：分页归一化（配置化但不跨模块耦合）
1. 新增 `PageNormalizer`（framework/util）
- 职责：读取 `PageProperties` 的 `defaultPageSize/maxPageSize`，对传入的 `PageQuery` 执行安全归一化（空值与越界处理），返回归一化后的副本。
- 函数级注释：
```java
/** 分页归一化适配器：应用 PageProperties 的默认值与上限 */
public final class PageNormalizer {
    /** 构造函数：注入 PageProperties */
    public PageNormalizer(dev.tagtag.framework.config.PageProperties props) { /* ... */ }
    /** 归一化 pageNo/pageSize，并返回副本 */
    public dev.tagtag.common.model.PageQuery normalize(dev.tagtag.common.model.PageQuery pq) { /* ... */ }
}
```
2. 在服务层使用归一化结果
- 受影响文件：`UserServiceImpl`、`DeptServiceImpl`、`RoleServiceImpl`、`MenuServiceImpl`
- 步骤：构造 `PageNormalizer` 并调用 `normalize(req.getPage())`，再传入 `Pages.toPage()`；其余逻辑保持不变。
- 验证：编译通过；分页边界值（pageSize=null/过大/负数）正常。

## Phase B：权限常量推广（统一安全策略）
1. 在控制器中增加/替换 `@PreAuthorize`
- 统一使用 `Permissions` 常量：
  - `DeptController`：创建/更新/删除 → `DEPT_CREATE/DEPT_UPDATE/DEPT_DELETE`
  - `RoleController`：创建/更新/删除/分配菜单 → `ROLE_CREATE/ROLE_UPDATE/ROLE_DELETE/ROLE_ASSIGN_MENU`
  - `MenuController`：创建/更新/删除 → `MENU_CREATE/MENU_UPDATE/MENU_DELETE`
- 函数级注释在方法头保留/补充。
2. 兼容性说明
- 若当前权限体系未下发对应授权，新增的 `@PreAuthorize` 会阻断访问。提供一个 `spring.security.enabled` 或 `security.preauthorize.enabled` 属性作为开关（默认开启），可快速禁用拦截用于联调。

## Phase C：清理剩余简单 XML 查询
1. 识别“简单查询”
- 条件：不含分页、批量操作或 JOIN 的单表查询，且可用 LambdaQuery 等价实现。
2. 替换与删除
- 将简单 XML 对应 Mapper 方法改为注解/MP 默认方法或服务层 LambdaQuery，删除 XML 片段与方法声明。
3. 保留项
- 仍保留：分页 XML、联表查询、批量插入/删除等性能相关 XML。

## Phase D：测试与验证
- 编译：`mvn -DskipTests compile`
- 冒烟：
  - 路由与权限：`/api/iam/*` 的创建/更新/删除在拥有权限时可访问；无权限时 403。
  - 分页边界：当 `pageSize=null/0/10000` 时，按 `PageProperties` 的默认值与上限归一化。
  - 排序：现有 `OrderByBuilder` 生效，白名单字段映射到正确列；非法字段被忽略，默认 `ORDER BY id ASC`。
  - 日志：`traceId` 在每次请求日志中可见。

## 交付节奏与影响
- 先落地 Phase A（分页归一化）与 Phase B（权限常量推广，含开关）；
- 再执行 Phase C（XML 清理），逐模块替换以降低风险；
- 保持与前端/网关的路由与权限约定同步更新，避免 403/404。

## 预期收益
- 分页行为可配置且安全；
- 权限策略统一、常量化，减少硬编码错误；
- XML 复杂度下降，维护成本降低；
- 与已有的 `OrderByBuilder`、`TraceIdFilter`、统一 API 前缀配合，整体一致性与可观测性进一步提升。
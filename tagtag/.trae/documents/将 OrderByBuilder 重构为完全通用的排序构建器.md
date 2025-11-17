## 重构目标
- 移除与实体耦合的 `forUser/forRole/forDept/forMenu`，提供一个通用的排序构建方法
- 统一注入字段白名单与字段→列映射，支持表别名与大小写不敏感匹配
- 加强安全性（列名校验，防 SQL 注入），保持 XML 只拼接 `${orderBySql}` 的方案

## 通用 API 设计
1. 通用方法（保留完整 SQL 片段）
```java
/**
 * 将 SortField 列表转换为安全的完整 ORDER BY 片段
 * - 仅允许映射存在且在白名单内的字段
 * - 当无有效排序时返回默认列的升序（或配置方向）
 */
public static String build(List<SortField> sortFields,
                           Map<String, String> fieldToColumn,
                           Collection<String> whitelist,
                           String defaultColumn,
                           boolean defaultAsc)
```
- 行为：忽略非法/未映射字段；方向缺省为升序；默认列校验与兜底
- 安全：对列名做 `[A-Za-z0-9_\.]` 校验，拒绝不安全列名

2. 快捷构造器/DSL（提高复用）
```java
/**
 * 字段到列映射构造器（大小写不敏感）
 */
public static Map<String, String> mapping(String[][] pairs)
// 示例：mapping(new String[][] {{"id","u.id"},{"username","u.username"},{"createTime","u.create_time"}})
```

## 服务层调用调整（示例）
- 以用户分页为例（其它服务同理）：
```java
// 构建映射与白名单
Map<String, String> map = OrderByBuilder.mapping(new String[][]{
    {"id","u.id"},{"username","u.username"},{"nickname","u.nickname"},{"createTime","u.create_time"},{"create_time","u.create_time"}
});
String orderBySql = OrderByBuilder.build(normalized.getSortFields(), map, pageProperties.getUser(), "u.id", true);
```
- 可选择是否带别名（例如 `u.`），映射完全由服务层提供，`OrderByBuilder` 不关心具体实体

## 实施步骤
1. 修改 `OrderByBuilder`：
- 删除 `forUser/forRole/forDept/forMenu`
- 增加通用 `build(...)` 与 `mapping(...)` 方法
- 列名校验与大小写不敏感映射

2. 更新四个服务的分页方法：
- 为每个查询准备其字段→列映射（可含表别名）
- 使用 `OrderByBuilder.build(...)` 生成 `orderBySql`
- 保持 XML `${orderBySql}` 方案不变（已有默认 `ORDER BY id ASC` 兜底）

3. 验证
- 编译与基础冒烟：分页携带排序字段返回正确 SQL
- 边界：
  - 空排序列表 → 默认列升序
  - 非法字段/未映射字段 → 忽略并回退默认
  - 别名列生成正确（如 `u.create_time DESC`）

## 可选增强
- 增加 `OrderByConfig` 对象，将映射、白名单、默认列/方向封装为配置对象，支持重用
- 增加单元测试覆盖常见与边界场景

## 风险与兼容
- 服务层需提供字段映射，工作量小且明确
- XML 结构无需变更，兼容现有分页拦截器

## 交付
- 一次性完成通用化重构与四个服务的接入，确保编译与冒烟通过
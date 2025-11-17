## 目标
- 去除手写字段→列映射（"low" 写法），统一通过 PageQuery + 实体元数据生成 ORDER BY
- 使用 MyBatis-Plus 元数据自动解析属性到列名，支持可选表别名，保持安全校验

## 设计与接口
1. ColumnResolver（属性→列名解析）
- 定义通用解析接口：`interface ColumnResolver { String resolve(String property); }`
- 默认实现：`mpResolver(Class<?> entityClass, String alias)`
  - 基于 `TableInfoHelper.getTableInfo(entityClass)` 获取属性→列映射
  - 支持主键与普通字段；可拼接别名（如 `u.create_time`）

2. 通用构建方法
- `OrderByBuilder.build(PageQuery pq, ColumnResolver resolver, Collection<String> whitelist, String defaultProperty, boolean defaultAsc)`
- 行为：遍历 `pq.getSortFields()`，按白名单与 resolver 构造安全的 `ORDER BY`；空列表时按默认属性与方向
- 安全：保留列名正则校验 `[A-Za-z0-9_\.]`

## 代码改动
- 更新 `OrderByBuilder`：
  - 添加 `ColumnResolver` 接口与 `mpResolver(...)` 默认实现
  - 新增 `build(PageQuery, ...)` 重载；保留原有 `build(List<SortField>, ...)` 以兼容
  - 移除服务中的 `mapping(...)` 调用
- 修改四个服务分页：
  - 使用 `OrderByBuilder.mpResolver(Entity.class, null)` 获取解析器
  - 调用 `OrderByBuilder.build(normalized, resolver, pageProperties.getXxx(), "id", true)` 生成 `orderBySql`

## 验证
- 编译通过
- 分页排序：`menuCode/menuName/createTime/create_time` 等属性自动正确映射为列
- 安全性：非法字段被忽略；当无有效排序时回退默认属性

## 兼容性
- XML 仍使用 `${orderBySql}` 方案，保持现有默认兜底；不影响分页拦截器

## 交付
- 一次性完成通用化重构与四个服务切换，并做编译验证
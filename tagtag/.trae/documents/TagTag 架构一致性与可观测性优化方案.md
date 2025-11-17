## 目标
- 大幅度提升一致性、可观测性与可维护性；允许对现有类进行重构或删除
- 采用更强方案：统一 API 前缀、集中权限常量、响应封装边界完善、链路追踪、排序映射 Java 化、分页配置化适配、清理简单 XML 改为 LambdaQuery

## Phase 1（立即落地）
1. 统一 API 前缀
- 所有控制器 `@RequestMapping` 改为 `GlobalConstants.API_PREFIX + "/{模块}/{资源}"`
- 示例：`UserController` 改为 `"/api/iam/users"`

2. 链路追踪 Filter
- 新增 `TraceIdFilter` 注入/清理 MDC 的 `traceId`，通过 `FilterRegistrationBean` 注册到全局
- 代码示例（函数级注释）：
```java
/** 请求链路追踪过滤器：为每次请求注入并清理 traceId */
public class TraceIdFilter implements jakarta.servlet.Filter {
    /** 生成并注入 traceId，链路结束后清理 */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String traceId = java.util.UUID.randomUUID().toString();
        org.slf4j.MDC.put(dev.tagtag.common.constant.GlobalConstants.TRACE_ID_MDC_KEY, traceId);
        try { chain.doFilter(req, res); } finally { org.slf4j.MDC.remove(dev.tagtag.common.constant.GlobalConstants.TRACE_ID_MDC_KEY); }
    }
}
```

3. 响应封装边界优化
- `GlobalResponseAdvice` 增加对 `ResponseEntity` 的排除；保持 `String` 的特殊处理，避免二次封装导致冲突

## Phase 2（一致性与去硬编码）
4. 权限常量集中
- 新增 `kernel.constant.Permissions`，统一定义：`PERM_USER_CREATE`、`PERM_USER_UPDATE` 等
- 控制器 `@PreAuthorize` 全量替换硬编码字符串，引用常量

5. 成功消息保持 `AppMessages`（已有）
- 新增操作类型时必须使用常量，禁止硬编码中文文案

## Phase 3（排序与分页的最优实现）
6. 排序字段映射 Java 化（最优方案）
- 新增 `framework.util.OrderByBuilder`：将 `List<SortField>` 安全映射为 SQL `ORDER BY` 片段，字段到列映射集中在 Java；统一白名单校验
- Mapper XML 删除 `<choose>` 字段映射，仅接受 `orderBySql` 字符串参数直接拼接
- 好处：去除多处重复 XML 逻辑、易测试与复用

7. 分页归一化适配器（配置化）
- 新增 `framework.util.PageNormalizer`，注入 `PageProperties`，对 `PageQuery` 的 `pageNo/pageSize` 做归一化处理后再传入 `Pages.toPage`
- 保持 `common` 与 `framework` 边界清晰：不在 `PageQuery` 内引用 `PageProperties`

## Phase 4（清理与重构）
8. 清理简单 XML 查询
- 将仅做简单条件筛选的 XML 改为 MyBatis-Plus LambdaQuery（如 `getByUsername` 等）；保留分页、批量、联表等 XML
- 删除不再使用的简单 XML 与对应 Mapper 方法

9. 单元测试与冒烟
- 路由：`/api/auth/*` 与 `/api/iam/*` 正常
- 日志：所有请求日志包含 `traceId`
- 分页：默认值与上限从 `PageProperties` 生效；边界值测试
- 排序：白名单与列映射通过；非法字段抛业务异常

## 影响与兼容
- API 前缀统一可能影响调用方，需同时更新前端/网关路由
- Mapper XML 调整为 Java 构建 `ORDER BY` 后，需逐一替换分页查询的参数约定
- 简单 XML 删除后，确保 Service 使用 LambdaQuery 替代并通过编译与接口验证

## 交付节奏
- 本轮：Phase 1 + Phase 2（权限常量类创建与替换）
- 次轮：Phase 3（排序 Java 化、分页适配器）
- 第三轮：Phase 4（清理 XML、补齐测试）

## 验收标准
- 控制器路径与权限统一、成功消息无硬编码
- 日志包含 traceId，ResponseAdvice 无不必要包装
- 排序映射无重复 XML 逻辑，分页从配置读取且边界安全
- 简单 XML 移除后接口功能等价，无回归问题
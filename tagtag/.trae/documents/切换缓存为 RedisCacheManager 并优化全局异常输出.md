## 目标
- 使用 RedisCacheManager 管理缓存，并支持按缓存名设置独立 TTL；保持与现有 RedisTemplate、ObjectMapper 序列化风格一致。
- 全局兜底异常改为通用消息输出，同时记录详细异常日志，不向响应体暴露内部堆栈信息。

## 改动范围
- 缓存：`tagtag-framework/src/main/java/dev/tagtag/framework/config/CacheConfig.java`
- 异常处理：`tagtag-framework/src/main/java/dev/tagtag/framework/web/GlobalExceptionHandler.java`
- 配置文件：`tagtag-start/src/main/resources/application-dev.yml`、`application-prod.yml`

## 实施步骤
### 缓存管理器切换为 RedisCacheManager
1. 新增属性绑定类 `CacheTtlProperties`（或在现有配置类中直接绑定）读取 `cache.ttl` 映射：`Map<String, Duration>`，用于不同缓存名 TTL。
2. 替换 `CacheConfig`：
   - 保留 `@EnableCaching`。
   - 注入 `RedisConnectionFactory` 与全局 `ObjectMapper`。
   - 构建 `RedisCacheConfiguration`：
     - key 使用 `StringRedisSerializer`；value 使用 `GenericJackson2JsonRedisSerializer(objectMapper)`；
     - `disableCachingNullValues()`；
     - 设置默认 TTL（如无专属 TTL）。
   - 基于 `cache.ttl` 生成 `initialCacheConfigurations`，为如 `roleMenuCodes`、`roleMenus`、`deptTree` 等缓存配置各自 TTL。
   - 使用 `RedisCacheManager.builder(connectionFactory)` + `.cacheDefaults(defaultConfig)` + `.withInitialCacheConfigurations(map)` 构建 Bean。
3. 删除现有 `ConcurrentMapCacheManager` 返回，实现迁移到 Redis 缓存。

### 配置文件增加 TTL 与启用 Redis 缓存
1. 在 `application-dev.yml`、`application-prod.yml` 增加：
   - `spring.cache.type: redis`。
   - `cache.ttl`：
     - `roleMenuCodes: 10m`
     - `roleMenus: 10m`
     - `deptTree: 30m`
   - 如需全局默认 TTL：可增加 `cache.default-ttl: 5m`（与属性类绑定）。
2. 确认 `spring.data.redis.*` 连接配置在 dev/prod 有效（当前工程已引入 `spring-boot-starter-data-redis`）。

### 兜底异常输出统一与日志记录
1. 在 `GlobalExceptionHandler` 引入日志（`@Slf4j` 或 `LoggerFactory`）。
2. 调整 `handleDefault(Exception ex)`：
   - 日志侧记录 `error("Unhandled exception", ex)`，利用 `TraceIdFilter` 注入的 MDC `traceId` 进行链路关联。
   - 响应体使用 `Result.fail(ErrorCode.INTERNAL_SERVER_ERROR)` 或统一文案（如：`"服务器内部错误"`），不要输出 `ex.getMessage()`。
   - 返回 HTTP 状态保持为 `500`。

## 验证
- 编译：`mvn -DskipTests compile` 无错误。
- 本地运行 dev：触发缓存的业务（如部门树加载）并到 Redis 查看 key 及 TTL；分别验证 `roleMenuCodes/roleMenus/deptTree` 的过期时间符合配置。
- 触发一个未捕获异常（模拟 NPE）：检查返回体为通用错误消息，日志中包含完整堆栈与 `traceId`。

## 风险与兼容性
- 引入 Redis 缓存后，环境需正确配置 Redis；开发环境可保留本地 Redis 或切换成 `docker compose`。
- TTL 配置缺失时使用默认 TTL，避免无限期缓存导致数据陈旧；也可在 prod 设更短 TTL。
- 并发场景下 Redis 缓存替换不会改变业务接口行为；只影响缓存命中与过期策略。

## 回滚策略
- 可将 `CacheConfig` 恢复为 `ConcurrentMapCacheManager` 返回，移除 `spring.cache.type: redis` 与 `cache.ttl` 配置即可回滚到本地缓存。

请确认是否按以上方案实施，我将直接完成代码与配置修改并进行编译及运行验证。
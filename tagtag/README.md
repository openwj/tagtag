tagtag-parent
│
├─pom.xml
│     ← 父工程 POM（统一管理依赖版本、插件、模块聚合）
│
├─tagtag-common                                   ← 🧰 通用工具层（纯工具类，无Spring依赖）
│  ├─src/main/java/dev/tagtag/common/
│  │  ├─constant/
│  │  │   ├─GlobalConstants.java                 ← 系统通用常量（如字符集、分页默认值等）
│  │  │   └─CacheConstants.java                  ← 缓存Key定义（用户信息、权限列表等）
│  │  │
│  │  ├─exception/
│  │  │   ├─ErrorCode.java                       ← 错误码枚举定义
│  │  │   ├─BusinessException.java               ← 通用业务异常封装
│  │  │   └─AssertUtils.java                     ← 参数校验工具类（非Spring依赖）
│  │  │
│  │  ├─model/
│  │  │   ├─Result.java                          ← 统一接口响应体
│  │  │   ├─PageResult.java                      ← 分页结果封装
│  │  │   ├─PageQuery.java                       ← 分页请求参数封装
│  │  │   └─IdNamePair.java                      ← 通用 id-name 键值对结构
│  │  │
│  │  └─util/
│  │      ├─BeanUtils.java                       ← Bean属性拷贝工具
│  │      └─StringUtils.java                     ← 字符串工具（保留 hasText）
│  │
│  └─pom.xml                                      ← 模块POM
│
├─tagtag-framework                                ← ⚙️ 框架整合层（Spring、MyBatis、Security 等基础设施整合）
│  ├─src/main/java/dev/tagtag/framework/
│  │  ├─config/
│  │  │   ├─MybatisPlusConfig.java               ← MyBatis Plus 配置
│  │  │   ├─RedisConfig.java                     ← Redis 配置
│  │  │   ├─JacksonConfig.java                   ← Jackson JSON 序列化配置
│  │  │   ├─AsyncConfig.java                     ← 异步线程池配置
│  │  │   ├─SecurityConfig.java                  ← Spring Security 核心配置
│  │  │   ├─SwaggerConfig.java                   ← Swagger / Knife4j API 文档配置
│  │  │   └─CorsConfig.java                      ← 跨域配置
│  │  │
│  │  ├─aspect/
│  │  │   ├─OperationLogAspect.java              ← 操作日志切面
│  │  │   └─RateLimitAspect.java                 ← 接口限流切面
│  │  │
│  │  ├─security/
│  │  │   ├─JwtProvider.java                     ← JWT 生成与验证工具
│  │  │   ├─JwtAuthenticationFilter.java         ← JWT 认证过滤器
│  │  │   ├─CustomAccessDeniedHandler.java       ← 无权限访问处理
│  │  │   └─CustomAuthenticationEntryPoint.java  ← 未认证访问处理
│  │  │
│  │  └─web/
│  │      ├─GlobalExceptionHandler.java          ← ✅ Web全局异常处理（@RestControllerAdvice）
│  │      ├─GlobalResponseAdvice.java            ← 统一响应体封装（ResponseBodyAdvice）
│  │      └─CorsConfig.java                      ← 跨域配置
│  │
│  └─pom.xml
│
├─tagtag-kernel                                   ← 🧩 核心支撑层（业务基础设施、AOP 注解、枚举、工具类）
│  ├─src/main/java/dev/tagtag/kernel/
│  │  ├─annotation/
│  │  │   ├─OperationLog.java                    ← 操作日志注解
│  │  │   ├─RateLimit.java                       ← 接口限流注解
│  │  │   └─DataPermission.java                  ← 数据权限注解
│  │  │
│  │  ├─enums/
│  │  │   ├─StatusEnum.java                      ← 启用/禁用枚举
│  │  │   ├─GenderEnum.java                      ← 性别枚举
│  │  │   └─LogTypeEnum.java                     ← 日志类型枚举
│  │  │
│  │  ├─handler/
│  │  │   └─MetaObjectHandlerImpl.java           ← MyBatis 自动填充实现
│  │  │
│  │  └─util/
│  │      ├─JwtUtils.java                        ← JWT 工具类
│  │      ├─UserContextHolder.java               ← 当前用户上下文管理
│  │      └─PermissionChecker.java               ← 权限校验工具
│  │
│  └─pom.xml
│
├─tagtag-contract                                ← 📜 契约层（DTO + RPC / 接口定义）
│  ├─tagtag-contract-iam
│  │  ├─dto/                                     ← 数据传输对象（DTO）
│  │  │   ├─UserDTO.java
│  │  │   ├─RoleDTO.java
│  │  │   ├─PermissionDTO.java
│  │  │   └─DeptDTO.java
│  │  │
│  │  └─api/                                     ← 远程接口（RPC 或 Feign）
│  │      ├─UserApi.java
│  │      ├─RoleApi.java
│  │      ├─PermissionApi.java
│  │      └─DeptApi.java
│  │
│  ├─tagtag-contract-system
│  │  ├─dto/SystemConfigDTO.java
│  │  └─api/SystemConfigApi.java
│  │
│  ├─tagtag-contract-auth
│  │  ├─dto/TokenDTO.java
│  │  └─api/AuthApi.java
│  │
│  └─pom.xml
│
├─tagtag-module                                  ← 🧱 业务模块层（业务实现）
│  ├─tagtag-module-iam
│  │  ├─controller/                              ← 控制层
│  │  │   ├─UserController.java
│  │  │   ├─RoleController.java
│  │  │   ├─PermissionController.java
│  │  │   └─DeptController.java
│  │  │
│  │  ├─entity/                                  ← 数据库实体
│  │  │   ├─User.java
│  │  │   ├─Role.java
│  │  │   ├─Permission.java
│  │  │   └─Dept.java
│  │  │
│  │  ├─mapper/                                  ← MyBatis Mapper
│  │  │   ├─UserMapper.java
│  │  │   ├─RoleMapper.java
│  │  │   ├─PermissionMapper.java
│  │  │   └─DeptMapper.java
│  │  │
│  │  ├─service/                                 ← 服务接口
│  │  │   ├─UserService.java
│  │  │   ├─RoleService.java
│  │  │   ├─PermissionService.java
│  │  │   ├─DeptService.java
│  │  │   └─impl/                                ← 服务实现
│  │  │       ├─UserServiceImpl.java
│  │  │       ├─RoleServiceImpl.java
│  │  │       ├─PermissionServiceImpl.java
│  │  │       └─DeptServiceImpl.java
│  │  │
│  │  ├─convert/                                 ← DTO/VO 与 Entity 转换器
│  │  │   ├─UserConvert.java
│  │  │   ├─RoleConvert.java
│  │  │   └─PermissionConvert.java
│  │  │
│  │  └─api/impl/                                ← 契约接口实现
│  │      ├─UserApiImpl.java
│  │      ├─RoleApiImpl.java
│  │      ├─PermissionApiImpl.java
│  │      └─DeptApiImpl.java
│  │
│  ├─tagtag-module-auth
│  │  ├─controller/AuthController.java
│  │  ├─service/AuthService.java
│  │  ├─service/impl/AuthServiceImpl.java
│  │  ├─security/AuthUserDetailsService.java     ← Spring Security 用户详情服务
│  │  └─filter/JwtAuthenticationFilter.java
│  │
│  ├─tagtag-module-system
│  │  ├─controller/SystemConfigController.java
│  │  ├─entity/SystemConfig.java
│  │  ├─mapper/SystemConfigMapper.java
│  │  ├─service/SystemConfigService.java
│  │  └─api/impl/SystemConfigApiImpl.java
│  │
│  └─pom.xml
│
├─tagtag-integration                             ← 🌐 外部系统集成层（第三方接口、SDK 封装）
│  ├─tagtag-integration-oss
│  │   ├─OssService.java
│  │   └─AliyunOssServiceImpl.java               ← 阿里云 OSS 实现
│  │
│  ├─tagtag-integration-message
│  │   ├─MessageSender.java
│  │   └─RabbitMessageSender.java                ← RabbitMQ 消息发送实现
│  │
│  └─tagtag-integration-thirdparty
│      ├─WeChatApiClient.java                    ← 微信 API 客户端封装
│      └─GithubOAuthClient.java                  ← GitHub OAuth 客户端封装
│
├─tagtag-test                                    ← 🧪 测试模块
│  ├─BaseIntegrationTest.java                     ← 测试基类（Spring 上下文、初始化数据）
│  ├─IamModuleTests.java
│  ├─AuthModuleTests.java
│  ├─SystemModuleTests.java
│  └─resources/test-data.sql                      ← 测试数据
│
└─tagtag-start                                   ← 🚀 启动层（Spring Boot 应用入口）
├─TagtagApplication.java
├─resources/
│  ├─application.yml
│  ├─application-dev.yml
│  ├─application-prod.yml
│  └─logback-spring.xml
│
└─pom.xml

---
seo:
  title: Tagtag Starter - 通用后台管理框架
  description: 模块化、企业级的 Java Spring Boot 管理框架。
---

::u-page-hero
---
align: left
links:
  - label: 快速开始
    to: /getting-started
    size: xl
    trailingIcon: i-lucide-arrow-right
    color: primary
  - label: GitHub
    to: https://github.com/admin/openwj/tagtag
    target: _blank
    size: xl
    color: neutral
    variant: outline
    icon: i-simple-icons-github
ui:
  title: 'text-5xl tracking-tight sm:text-7xl font-semibold text-gray-900 dark:text-white sm:leading-[1.1] mb-6'
  description: 'text-xl text-gray-500 dark:text-gray-400 leading-8 mb-10'
---
#title
Tagtag
<span class="text-primary">通用后台管理框架</span>

#description
一个模块化、企业级的 Java Spring Boot 后台管理框架。
专为可扩展性、高性能和极致开发体验而构建。

#default
  ::u-landing-hero-code
  ---
  filename: UserController.java
  language: java
  ---
  ```java
  @RestController
  @RequestMapping("/users")
  public class UserController {
  
      @Resource
      private UserService userService;
  
      @OperationLog(module = "IAM", name = "Create User")
      @PostMapping
      public Result<Long> create(@RequestBody @Validated UserDTO dto) {
          return Result.success(userService.create(dto));
      }
  }
  ```
  ::
::

::u-page-section
---
title: 核心特性
description: 为什么选择 Tagtag 构建您的下一个企业级应用？
align: center
ui:
  title: 'text-3xl sm:text-4xl font-bold tracking-tight text-gray-900 dark:text-white'
---
::u-page-grid
  ::u-landing-card
  ---
  icon: i-lucide-layers
  title: 模块化架构
  description: 严格的分层设计：common、framework、kernel、contract 和 module。解耦且易于维护。
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-shield-check
  title: 企业级能力
  description: 内置 RBAC（基于角色的访问控制）、操作日志和接口限流。
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-file-code
  title: 契约优先
  description: 使用 DTO 和 Interface 清晰分离 API 定义与实现，确保接口稳定性。
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-box
  title: 现代技术栈
  description: 基于 Spring Boot 3、MyBatis Plus、Redis 构建，集成 Vben Admin (Vue 3) 企业级前端。
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-plug
  title: 系统模块
  description: 内置字典管理、系统参数配置和消息通知模块，快速搭建业务。
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-zap
  title: 开发体验
  description: 标准化的异常处理、响应封装和工具库，大幅加速日常开发流程。
  color: primary
  ---
  ::
::
::

::u-page-cta
---
title: 准备好构建您的后台系统了吗？
description: 立即开始使用 Tagtag，构建稳健的企业级应用。
links:
  - label: 开始构建
    to: '/getting-started'
    trailingIcon: i-lucide-arrow-right
    size: xl
  - label: 查看 GitHub
    to: 'https://github.com/admin/openwj/tagtag'
    target: _blank
    variant: subtle
    size: xl
    icon: i-simple-icons-github
align: center
---
::

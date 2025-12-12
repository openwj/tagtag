---
seo:
  title: Tagtag - Generic Admin Backend
  description: A modular, enterprise-grade Java Spring Boot admin framework.
---

::u-page-hero
---
align: left
links:
  - label: Get Started
    to: /en/getting-started
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
<span class="text-primary">Generic Admin Backend</span>

#description
A modular, enterprise-grade Java Spring Boot admin framework.
Built for scalability, performance, and developer experience.

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
title: Core Features
description: Why choose Tagtag for your next enterprise application?
align: center
ui:
  title: 'text-3xl sm:text-4xl font-bold tracking-tight text-gray-900 dark:text-white'
---
::u-page-grid
  ::u-landing-card
  ---
  icon: i-lucide-layers
  title: Modular Architecture
  description: Strictly layered design with common, framework, kernel, contract, and module. Decoupled and maintainable.
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-shield-check
  title: Enterprise Ready
  description: Built-in RBAC (Role-Based Access Control), Data Permission, Operation Logs, and Rate Limiting.
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-file-code
  title: Contract First
  description: Clear separation of API definitions and implementations using DTOs and Interfaces.
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-box
  title: Modern Stack
  description: Built on Spring Boot 3, MyBatis Plus, and Redis, integrated with modern Vue 3 admin frontends.
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-plug
  title: Rich Integrations
  description: Ready-to-use modules for OSS (Aliyun), Message Queues (RabbitMQ), and Third-party Auth (WeChat, GitHub).
  color: primary
  ---
  ::

  ::u-landing-card
  ---
  icon: i-lucide-zap
  title: Developer Experience
  description: Standardized error handling, response wrapping, and utility libraries to speed up development.
  color: primary
  ---
  ::
::
::

::u-page-cta
---
title: Ready to build your admin system?
description: Get started with Tagtag today and build robust enterprise applications.
links:
  - label: Start Building
    to: '/en/getting-started'
    trailingIcon: i-lucide-arrow-right
    size: xl
  - label: View on GitHub
    to: 'https://github.com/admin/openwj/tagtag'
    target: _blank
    variant: subtle
    size: xl
    icon: i-simple-icons-github
align: center
---
::

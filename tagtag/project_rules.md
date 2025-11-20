# 项目说明与架构概览

## 项目总体概览
- 前后端同库：后端为 Spring Boot 多模块 Maven 工程；前端位于 `tagtag-ui` 的 pnpm 工作区（monorepo），含主应用与内部共享包。
- 前端技术栈：Vue 3、Vite、Pinia、Vue Router、TailwindCSS、Ant Design Vue、Vitest/Playwright。
- 后端技术栈：Spring Boot、MyBatis-Plus、统一异常与响应封装（`GlobalExceptionHandler`、`GlobalResponseAdvice`）、Actuator。

## 目录结构总览
- 前端应用：`tagtag-ui/apps/tagtag`（业务入口与页面、路由、状态、接口）。
- 前端内部包：`tagtag-ui/internal/*`（统一 Vite/Tailwind/TS/Lint 配置与 Node 工具）。
- 前端共享包：`tagtag-ui/packages/*`（`@core` UI 套件与基础能力、`effects` 业务能力、`constants/utils/types` 等）。
- 后端模块：`tagtag-start`（启动）、`tagtag-framework`（统一 Web 处理）、`tagtag-common`（通用模型与工具）、`tagtag-module/*`（业务模块）、`tagtag-contract/*`（契约模块）。

## 前端架构
- 应用入口：初始化偏好、动态引导并卸载全局 loading。
  - `tagtag-ui/apps/tagtag/src/main.ts:16-29`
- 引导与挂载：创建 Vue 应用、注册指令与插件、初始化 i18n/Pinia/路由、动态标题、最终挂载。
  - `tagtag-ui/apps/tagtag/src/bootstrap.ts:19-74`
- 路由：根据环境切换 History/Hash，注入路由守卫。
  - `tagtag-ui/apps/tagtag/src/router/index.ts:15-20`、`34-35`
- Vite 代理：将 `/api` 代理到后端 `http://localhost:8080` 以便前后端联调。
  - `tagtag-ui/apps/tagtag/vite.config.mts:7-15`

## 后端架构
- 启动入口：扫描 `dev.tagtag` 包并注册 MyBatis Mapper。
  - `tagtag-start/src/main/java/dev/tagtag/start/StartApplication.java:7-9`
- 应用配置：端口 `8080`、上下文根 `/`、环境 `dev`、Actuator 暴露 `health,info`。
  - `tagtag-start/src/main/resources/application.yml:11-24`
- 全局异常与统一响应：
  - 业务异常与参数校验错误统一封装。
    - `tagtag-framework/src/main/java/dev/tagtag/framework/web/GlobalExceptionHandler.java:31-36`、`43-64`
  - 兜底错误与权限不足处理。
    - `tagtag-framework/.../GlobalExceptionHandler.java:91-97`、`117-121`
  - 统一将非 `Result` 响应包装为 `Result`。
    - `tagtag-framework/.../GlobalResponseAdvice.java:12-16`、`30-43`

## 数据流与接口约定
- HTTP 客户端：读取 `apiURL`，注入 `Authorization` 与语言头、标准响应结构、令牌刷新与错误提示。
  - `tagtag-ui/apps/tagtag/src/api/request.ts:22`、`32-35`、`86-95`、`99-105`、`107-115`、`119-126`
- 认证接口：登录/刷新/注销/权限码/注册/当前用户。
  - `tagtag-ui/apps/tagtag/src/api/core/auth.ts:34-55`、`63-75`、`82-83`
- IAM 示例：角色分页/详情/增删改与菜单分配、存在性检查。
  - `tagtag-ui/apps/tagtag/src/api/modules/iam/role.ts:22-24`、`31-36`、`42-67`、`74-85`、`91-101`
- 登录后：并行获取用户信息与权限码，写入 Store 并跳转首页。
  - `tagtag-ui/apps/tagtag/src/store/auth.ts:37-55`、`59-64`

## 开发与构建
- 前端根脚本：`tagtag-ui/package.json`（`dev` 第19行、`dev:tagtag` 第20行、`build`/`preview` 等）。
- 前端开发：在 `tagtag-ui` 根运行 `pnpm run dev:tagtag` 启动主应用。
- 前端构建：`pnpm run build:tagtag` 或工作区级 `pnpm run build`。
- 后端运行：IDE 运行 `StartApplication` 或在后端根执行 `mvn spring-boot:run`。
- 测试：`vitest` 单测（`test:unit`），`playwright` 端到端（`test:e2e`）。

## 环境与要求
- Node/Pnpm 要求：`node >= 20.10.0`、`pnpm >= 9.12.0`，`packageManager: pnpm@10.14.0`。
  - `tagtag-ui/package.json:68-73`
- 后端配置文件：`application.yml`/`application-dev.yml`/`application-prod.yml`。

## 常见约定
- 前端接口统一以 `/api` 前缀通过 Vite 代理访问后端。
- 后端统一以 `Result{ code, data, message }` 结构返回业务数据，异常经全局处理转换为标准结构。
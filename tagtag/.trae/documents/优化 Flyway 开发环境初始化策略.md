## 目标

* 提供一个可直接运行的测试类，生成并校验 BCrypt 哈希，方便你自行生成写库。

## 方案

* 在 `tagtag-start` 模块添加 JUnit5 测试（使用 Spring Security 的 BCrypt），通过系统属性传入明文与可选哈希。

* 命令行示例：

  * 生成：`mvn -DPLAINTEXT=password -Dtest=PasswordHashTest test`

  * 校验：`mvn -DPLAINTEXT=password -DHASH=$2a$... -Dtest=PasswordHashTest test`

## 实施

1. 为 `tagtag-start/pom.xml` 增加 `spring-boot-starter-test` 测试依赖。
2. 新增测试类 `src/test/java/dev/tagtag/start/PasswordHashTest.java`：

   * 读取系统属性 `PLAINTEXT`（默认 `password`），生成 BCrypt 哈希并打印；

   * 若提供 `HASH`，则对哈希进行匹配校验并打印结果；

   * 所有方法添加函数级注释。
3. 构建与测试运行验证。

## 交付

* 你可以用上述命令生成/校验哈希并将返回的哈希写入数据库，以完成登录校验。


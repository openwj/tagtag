## 目标
- 取消 `SecurityConfig` 中的 `@Value` 注入，改为注入 `JwtProperties` 配置类，统一配置来源。

## 变更
- 修改 `SecurityConfig.jwtDecoder(...)` 方法签名为不接收参数，改为使用构造/参数注入 `JwtProperties`：
  - 添加字段 `private final JwtProperties jwtProperties;`
  - `SecretKeySpec key = new SecretKeySpec(jwtProperties.getSecret().getBytes(GlobalConstants.CHARSET_UTF8), "HmacSHA256");`

## 验证
- 编译打包；运行 dev 环境，验证认证流程正常（登录、刷新）。

## 影响
- 配置读取统一为 `JwtProperties`，减少零散 `@Value` 使用。
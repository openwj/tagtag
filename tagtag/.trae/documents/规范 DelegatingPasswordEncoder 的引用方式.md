## 目标
- 将代码中类似 `org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder()` 的全限定类调用改为通过 import 引用，提升代码简洁与一致性。

## 涉及文件与改动
- `tagtag-framework/src/main/java/dev/tagtag/framework/config/SecurityConfig.java`
  - 添加：`import org.springframework.security.crypto.factory.PasswordEncoderFactories;`
  - 添加：`import javax.crypto.spec.SecretKeySpec;`
  - 添加：`import dev.tagtag.common.constant.GlobalConstants;`
  - 添加集合类 import：`import java.util.*;`
  - 替换：方法体使用 `PasswordEncoderFactories.createDelegatingPasswordEncoder()`
  - 替换：`new SecretKeySpec(...)`、`GlobalConstants.CHARSET_UTF8`、`new HashSet<>()`、`Set`/`Collection` 简化。
- `tagtag-framework/src/main/java/dev/tagtag/framework/security/TokenVersionFilter.java`
  - 添加：`import dev.tagtag.common.util.Numbers;`
  - 替换：`Numbers.toLong(...)`，去掉 `dev.tagtag.common.util.Numbers` 全限定使用。
- `tagtag-module/tagtag-module-auth/src/main/java/dev/tagtag/module/auth/service/impl/AuthServiceImpl.java`
  - 已有 `Strings` import，统一使用 `Strings.normalize(...)`（如果存在全限定引用则简化）。

## 验证
- 全项目编译打包，确保无编译错误；运行 dev 环境做基础登录/注销验证。

如果确认，我将立即进行上述改动并完成构建验证。
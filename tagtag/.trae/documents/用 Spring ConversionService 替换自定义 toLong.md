## 目标
- 移除重复的私有 `toLong(Object)` 方法，改为使用 Spring 提供的 `ConversionService` 做类型转换，提升一致性与可维护性。

## 变更点
1. 在 `AuthServiceImpl` 注入 `org.springframework.core.convert.ConversionService`，替换：
   - `uid = conversionService.convert(claims.get("uid"), Long.class)`
   - `tokenVer = conversionService.convert(claims.get("ver"), Long.class)`
   - 删除类内 `toLong(Object)` 方法。
2. 在 `TokenVersionFilter` 注入 `ConversionService`，替换：
   - `uid = conversionService.convert(uidObj, Long.class)`
   - `ver = conversionService.convert(verObj, Long.class)`
   - 删除类内 `toLong(Object)` 方法。
3. 保持其他逻辑不变；为新增构造注入与方法添加函数级注释。

## 验证
- 编译打包成功。
- 运行 `dev` profile：登录成功；注销后旧令牌 401。

## 说明
- Spring Boot 自动提供 `ConversionService`（默认 `ApplicationConversionService`），可直接注入使用；比自定义 `String.valueOf + Long.parseLong` 更健壮。

## 交付
- 两处类的改造与注释完善，不新增公共工具类，不增加额外依赖。
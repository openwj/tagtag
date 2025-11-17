## 目标
- 按你的建议使用 Lombok，将对象→Long 的转换封装为公共工具，消除私有方法与显式注入，统一调用方式。

## 方案
- 在 `tagtag-common` 新增工具类 `dev.tagtag.common.util.Numbers`，使用 `@UtilityClass` 提供静态方法：
  - `Long toLong(Object src)`：内部使用 `ApplicationConversionService.getSharedInstance().convert(src, Long.class)` 做转换，返回 null 表示无法转换。
- 替换以下调用点，去除类内私有方法与 `ConversionService` 显式注入：
  - `AuthServiceImpl`：将 `conversionService.convert(...)` 改为 `Numbers.toLong(...)`；删除字段 `ConversionService conversionService`。
  - `TokenVersionFilter`：将 `conversionService.convert(...)` 改为 `Numbers.toLong(...)`；删除构造参数中的 `ConversionService`。
- 所有新增/变更方法添加函数级注释。

## 步骤
1. 新增 `Numbers` 工具类（Lombok `@UtilityClass`）。
2. 修改 `AuthServiceImpl` 与 `TokenVersionFilter` 的转换调用；清理不再需要的依赖注入与构造签名。
3. 编译并验证运行（dev profile），确保登录/刷新/注销行为与版本校验正常。

## 说明
- 该实现符合“使用 Lombok”的诉求，同时维持现有逻辑；`ApplicationConversionService` 可满足基本 `String/Number→Long` 转换需求。
- 若未来添加自定义 Converter，则仍可改回注入式 `ConversionService`，两者可互换。

是否按此方案执行？我会在确认后直接提交代码并验证构建。
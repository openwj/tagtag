## 方案概览
- 问题：`spring.flyway.clean-on-validation-error` 在 Flyway 10.18 已弃用、Flyway 11 移除。
- 目标：仅在开发环境出现校验失败（如校验和不一致）时自动 clean + migrate，生产环境保持严格校验不自动清库。

## 配置调整（dev）
1. 从 `application-dev.yml` 移除 `spring.flyway.clean-on-validation-error`，保留：
   - `spring.flyway.baseline-on-migrate: false`
   - `spring.flyway.clean-disabled: false`
   - `spring.flyway.locations: classpath:db/migration`
2. 生产 (`application-prod.yml`) 不做任何改动。

## 开发环境策略实现（代码）
- 在 `tagtag-start` 增加 dev 专用迁移策略 Bean，通过 Spring Boot 的 `FlywayMigrationStrategy` 实现“校验失败则 clean 后重迁移”：
```java
// 文件：tagtag-start/src/main/java/dev/tagtag/start/config/DevFlywayStrategy.java
// 作用范围：仅 dev 环境生效
package dev.tagtag.start.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.exception.FlywayValidateException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

@Configuration
@Profile("dev")
public class DevFlywayStrategy {
    /**
     * 开发环境迁移策略：校验失败自动 clean 再 migrate
     * @return 迁移策略 Bean
     */
    @Bean
    public FlywayMigrationStrategy devMigrationStrategy() {
        return (Flyway flyway) -> {
            try {
                // 先做严格校验
                flyway.validate();
                // 校验通过则正常迁移
                flyway.migrate();
            } catch (FlywayValidateException ex) {
                // 校验失败（如 checksum 不一致）：执行清库后重建
                flyway.clean();
                flyway.migrate();
            }
        };
    }
}
```

## 验证步骤
- 打包并启动 dev：首次启动将执行严格校验；若失败会自动 clean 并按最新 V1/V2 迁移，应用正常启动。
- 生产环境不会引入该策略，仍保持默认的严格校验与不可清库。

## 说明
- 该策略仅在 dev Profile 下启用，等价替代旧 `clean-on-validation-error` 行为；更透明、可维护。
- 若你更希望遇到校验失败只 repair 不 clean，我可改为 `flyway.repair(); flyway.migrate();`。

请确认，我将按以上方案移除弃用配置并加入 dev 迁移策略，然后完成打包与启动验证。
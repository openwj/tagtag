package dev.tagtag.start.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 在开发环境应用启动后，自动清空数据库并重新执行所有 Flyway 迁移脚本。
 * 仅在 `dev` Profile 下启用，避免对生产环境造成影响。
 */
@Component
@Profile("dev")
public class DevFlywayResetRunner implements ApplicationRunner {

    private final Flyway flyway;

    /**
     * 构造函数：注入 Flyway 实例，用于执行清库与迁移操作
     * @param flyway Flyway 实例
     */
    public DevFlywayResetRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    /**
     * 应用启动回调：在开发环境启动时执行清库与迁移，保证每次启动都是全新数据
     * @param args 启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // 允许清库前提：application-dev.yml 中需设置 spring.flyway.clean-disabled=false
        flyway.clean();
        flyway.migrate();
    }
}
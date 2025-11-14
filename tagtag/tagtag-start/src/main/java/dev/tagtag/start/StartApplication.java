package dev.tagtag.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "dev.tagtag")
@MapperScan({"dev.tagtag.module.**.mapper"})
public class StartApplication {

    /**
     * 应用入口函数：启动 Spring Boot 应用
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}

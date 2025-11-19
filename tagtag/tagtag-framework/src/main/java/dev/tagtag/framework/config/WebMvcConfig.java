package dev.tagtag.framework.config;

import dev.tagtag.framework.web.converter.StringToEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 拦截器 & 静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 向 Spring MVC 注册自定义类型转换器
     * @param registry 格式化与转换器注册表
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }
}

package dev.tagtag.framework.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web 过滤器注册配置
 */
@Configuration
public class FilterConfig {

    /**
     * 注册 TraceIdFilter 到全局，确保每个请求具备 traceId
     * @return 过滤器注册 Bean
     */
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TraceIdFilter());
        reg.setOrder(Integer.MIN_VALUE + 10);
        reg.addUrlPatterns("/*");
        reg.setName("traceIdFilter");
        return reg;
    }
}
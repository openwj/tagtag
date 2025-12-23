package dev.tagtag.framework.web;

import dev.tagtag.framework.constant.FilterConstants;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TraceIdFilter());
        reg.setOrder(FilterConstants.TRACE_ID_FILTER_ORDER);
        reg.addUrlPatterns(FilterConstants.URL_PATTERN_ALL);
        reg.setName(FilterConstants.TRACE_ID_FILTER_NAME);
        return reg;
    }
}

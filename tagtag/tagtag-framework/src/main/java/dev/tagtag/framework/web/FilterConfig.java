package dev.tagtag.framework.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private static final String TRACE_ID_FILTER_NAME = "traceIdFilter";
    private static final String URL_PATTERN_ALL = "/*";
    private static final int TRACE_ID_FILTER_ORDER = Integer.MIN_VALUE + 10;

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TraceIdFilter());
        reg.setOrder(TRACE_ID_FILTER_ORDER);
        reg.addUrlPatterns(URL_PATTERN_ALL);
        reg.setName(TRACE_ID_FILTER_NAME);
        return reg;
    }
}

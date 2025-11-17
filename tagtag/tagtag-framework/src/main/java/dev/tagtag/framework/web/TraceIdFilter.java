package dev.tagtag.framework.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import dev.tagtag.common.constant.GlobalConstants;

/**
 * 请求链路追踪过滤器：为每次请求注入并清理 traceId
 */
public class TraceIdFilter implements Filter {

    /**
     * 过滤器主流程：生成 traceId，放入 MDC，继续后续链路；最终清理
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param chain 过滤器链
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = java.util.UUID.randomUUID().toString();
        MDC.put(GlobalConstants.TRACE_ID_MDC_KEY, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(GlobalConstants.TRACE_ID_MDC_KEY);
        }
    }
}
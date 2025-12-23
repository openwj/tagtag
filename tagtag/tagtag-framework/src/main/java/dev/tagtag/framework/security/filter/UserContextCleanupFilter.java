package dev.tagtag.framework.security.filter;

import dev.tagtag.kernel.util.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class UserContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                UserContextHolder.clear();
                log.debug("UserContext cleared after request: {}", request.getRequestURI());
            } catch (Exception e) {
                log.warn("Failed to clear UserContext after request: {}", request.getRequestURI(), e);
            }
        }
    }
}

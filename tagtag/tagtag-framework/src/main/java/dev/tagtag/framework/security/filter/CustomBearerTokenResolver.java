package dev.tagtag.framework.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final List<String> permitPaths;

    public CustomBearerTokenResolver(List<String> permitPaths) {
        this.permitPaths = permitPaths;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        // 对于 OPTIONS 请求，直接返回 null（允许访问）
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return null;
        }

        // 对于公开路径，直接返回 null（允许访问）
        String requestPath = request.getRequestURI();
        for (String permitPath : permitPaths) {
            if (matchesPath(requestPath, permitPath)) {
                return null;
            }
        }

        // 从请求头中获取 Bearer 令牌
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 从请求参数中获取令牌
        String paramToken = request.getParameter("access_token");
        if (StringUtils.hasText(paramToken)) {
            return paramToken;
        }

        return null;
    }

    private boolean matchesPath(String requestPath, String pattern) {
        // 简单的路径匹配实现，支持 ** 通配符
        if (pattern.equals(requestPath) || pattern.equals("/**")) {
            return true;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return requestPath.startsWith(prefix);
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return requestPath.startsWith(prefix) && requestPath.indexOf('/', prefix.length() + 1) == -1;
        }
        return requestPath.equals(pattern);
    }
}

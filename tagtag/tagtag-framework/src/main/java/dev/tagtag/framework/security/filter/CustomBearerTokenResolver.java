package dev.tagtag.framework.security.filter;

import dev.tagtag.framework.security.util.PathMatcherUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.List;

@Slf4j
public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final BearerTokenResolver delegate = new DefaultBearerTokenResolver();
    private final String[] permitPaths;

    public CustomBearerTokenResolver(List<String> permitPaths) {
        this.permitPaths = permitPaths != null ? permitPaths.toArray(new String[0]) : new String[0];
        log.info("CustomBearerTokenResolver initialized with permitPaths: {}", (Object) permitPaths);
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.debug("Resolving token for request: {}", requestURI);
        
        if (PathMatcherUtils.isPermitPath(requestURI, permitPaths)) {
            log.debug("Request URI is in permit paths, returning null (no token required): {}", requestURI);
            return null;
        }
        
        String token = delegate.resolve(request);
        log.debug("Token resolved for request {}: {}", requestURI, token != null ? "found" : "not found");
        return token;
    }
}
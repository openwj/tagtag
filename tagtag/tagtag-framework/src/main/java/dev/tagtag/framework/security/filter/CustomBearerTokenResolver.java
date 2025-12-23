package dev.tagtag.framework.security.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.util.AntPathMatcher;

import java.util.List;

public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver delegate;
    private final String[] permitPaths;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public CustomBearerTokenResolver(List<String> permitPaths) {
        this.delegate = new DefaultBearerTokenResolver();
        this.delegate.setAllowFormEncodedBodyParameter(false);
        this.delegate.setAllowUriQueryParameter(false);
        this.permitPaths = permitPaths != null ? permitPaths.toArray(new String[0]) : new String[0];
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        if (isPermitPath(requestURI, permitPaths)) {
            return null;
        }
        
        return delegate.resolve(request);
    }

    private boolean isPermitPath(String path, String[] permitPaths) {
        if (permitPaths == null || permitPaths.length == 0) {
            return false;
        }
        for (String pattern : permitPaths) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
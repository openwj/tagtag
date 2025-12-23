package dev.tagtag.framework.security.filter;

import dev.tagtag.framework.security.util.PathMatcherUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import java.util.List;

public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver delegate;
    private final String[] permitPaths;

    public CustomBearerTokenResolver(List<String> permitPaths) {
        this.delegate = new DefaultBearerTokenResolver();
        this.delegate.setAllowFormEncodedBodyParameter(false);
        this.delegate.setAllowUriQueryParameter(false);
        this.permitPaths = permitPaths != null ? permitPaths.toArray(new String[0]) : new String[0];
    }

    @Override
    public String resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        if (PathMatcherUtils.isPermitPath(requestURI, permitPaths)) {
            return null;
        }
        
        return delegate.resolve(request);
    }
}
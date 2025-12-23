package dev.tagtag.framework.security.handler;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import dev.tagtag.framework.security.util.SecurityHandlerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        SecurityHandlerUtils.writeErrorResponse(response, ErrorCode.UNAUTHORIZED.getCode(), Result.unauthorized(authException.getMessage()));
    }
}

package dev.tagtag.framework.security.handler;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import dev.tagtag.framework.security.util.SecurityHandlerUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        SecurityHandlerUtils.writeErrorResponse(response, ErrorCode.FORBIDDEN.getCode(), Result.forbidden(accessDeniedException.getMessage()));
    }
}

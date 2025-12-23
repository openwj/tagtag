package dev.tagtag.framework.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tagtag.common.model.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHandlerUtils {

    private final ObjectMapper objectMapper;

    public SecurityHandlerUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeErrorResponse(HttpServletResponse response, int status, Result<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

package dev.tagtag.framework.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tagtag.common.model.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public final class SecurityHandlerUtils {

    private static ObjectMapper objectMapper;

    private SecurityHandlerUtils() {}

    public static void setObjectMapper(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public static void writeErrorResponse(HttpServletResponse response, int status, Result<?> result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

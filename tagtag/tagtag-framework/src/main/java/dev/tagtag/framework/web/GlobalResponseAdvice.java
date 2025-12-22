package dev.tagtag.framework.web;

import dev.tagtag.common.model.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 统一响应体封装（ResponseBodyAdvice）
 */
// 这里我停掉了全局 封装返回，没有必要使用还有负担
//@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 是否应用响应体封装
     */
    @Override
    /**
     * 判断是否启用响应封装
     * 始终启用，具体在写出阶段按类型进行放行控制。
     */
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 将非 Result 的响应统一包装为 Result
     */
    @Override
    /**
     * 在响应写出前进行统一封装
     * - 放行 ResponseEntity：不触碰其内部响应体
     * - 放行 Result：已是统一格式
     * - 放行 String：避免 String 再封装导致字符串转换器冲突
     * - 放行二进制/流类型：避免破坏消息转换器的预期类型
     */
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseEntity<?>) {
            return body;
        }
        if (body instanceof Result<?>) {
            return body;
        }
        if (String.class.equals(returnType.getParameterType())) {
            return body; // 避免 String 再封装导致类型冲突
        }
        // 关键：跳过二进制/流类型，避免类型被改变
        if (body instanceof org.springframework.core.io.Resource
                || body instanceof byte[]
                || body instanceof StreamingResponseBody) {
            return body;
        }
        // 额外防护：按选择的消息转换器类型跳过
        if (ResourceHttpMessageConverter.class.isAssignableFrom(selectedConverterType)
                || ByteArrayHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return body;
        }
        return Result.ok(body);
    }
}

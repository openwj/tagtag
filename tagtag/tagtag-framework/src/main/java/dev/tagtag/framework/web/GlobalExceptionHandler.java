package dev.tagtag.framework.web;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

/**
 * Web全局异常处理（@RestControllerAdvice）
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param ex 异常
     * @return 响应体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException ex) {
        int status = ex.getHttpStatus();
        String msg = ex.getMessage();
        if ((msg == null || msg.isBlank()) && ex.getErrorCode() == ErrorCode.UNAUTHORIZED) {
            msg = "未登录或会话已过期";
        }
        Result<Void> body = (ex.getErrorCode() == ErrorCode.UNAUTHORIZED)
                ? Result.unauthorized(msg)
                : (ex.getErrorCode() == ErrorCode.FORBIDDEN)
                    ? Result.forbidden(msg)
                    : Result.fail(ex.getErrorCode(), msg);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * 处理参数校验失败（JSR-380）
     * @param ex 异常
     * @return 响应体
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Result<Void>> handleValidation(Exception ex) {
        String msg;
        List<String> errors = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException manv) {
            errors = manv.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            msg = String.join("; ", errors);
            if (msg.isBlank()) msg = "参数校验失败";
        } else if (ex instanceof BindException be) {
            errors = be.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            msg = String.join("; ", errors);
            if (msg.isBlank()) msg = "参数绑定失败";
        } else {
            msg = "参数错误";
        }
        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg, errors);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
    }

    /**
     * 处理方法参数约束违反（如 @RequestParam/@PathVariable 校验）
     * @param ex 异常
     * @return 响应体
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString();
                    String m = v.getMessage();
                    return path + ": " + m;
                })
                .collect(Collectors.toList());
        String msg = String.join("; ", errors);
        if (msg.isBlank()) msg = "参数约束违反";
        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg, errors);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
    }

    /**
     * 处理数据库唯一键冲突异常
     * @param ex 异常
     * @return 409 响应体
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKey(DuplicateKeyException ex) {
        log.warn("Database duplicate key exception: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.CONFLICT, "数据已存在，请检查唯一性约束");
        return ResponseEntity.status(ErrorCode.CONFLICT.getCode()).body(body);
    }

    /**
     * 处理数据库数据完整性异常（外键约束、Data too long等）
     * @param ex 异常
     * @return 409 响应体
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.CONFLICT, "数据完整性冲突，请检查数据依赖");
        return ResponseEntity.status(ErrorCode.CONFLICT.getCode()).body(body);
    }

    /**
     * 处理不支持的HTTP方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String msg = "不支持的方法: " + ex.getMethod();
        Result<Void> body = Result.fail(ErrorCode.METHOD_NOT_ALLOWED, msg);
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getCode()).body(body);
    }

    /**
     * 处理不支持的媒体类型
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String msg = "不支持的媒体类型: " + ex.getContentType();
        Result<Void> body = Result.fail(ErrorCode.UNSUPPORTED_MEDIA_TYPE, msg);
        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode()).body(body);
    }

    /**
     * 处理请求体不可读（JSON格式错误等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Request body not readable: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.BAD_REQUEST, "请求体格式错误");
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    /**
     * 处理缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = "缺少必填参数: " + ex.getParameterName();
        Result<Void> body = Result.fail(ErrorCode.BAD_REQUEST, msg);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    /**
     * 处理404未找到（需配置 spring.mvc.throw-exception-if-no-handler-found=true）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        Result<Void> body = Result.fail(ErrorCode.NOT_FOUND, "资源不存在: " + ex.getRequestURL());
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getCode()).body(body);
    }

    /**
     * 兜底异常处理
     * @param ex 异常
     * @return 响应体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleDefault(Exception ex) {
        // 记录完整异常堆栈到日志，响应体使用通用错误消息
        log.error("Unhandled exception", ex);
        Result<Void> body = Result.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).body(body);
    }

    /**
     * 处理非法参数异常（Bad Request）
     * @param ex 异常
     * @return 400 响应体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleBadRequest(IllegalArgumentException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) msg = ErrorCode.BAD_REQUEST.getMessage();
        Result<Void> body = Result.fail(msg);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    /**
     * 处理权限不足异常（方法级与过滤链级）
     * @param ex 异常
     * @return 403 响应体
     */
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<Result<Void>> handleForbidden(Exception ex) {
        Result<Void> body = Result.forbidden("没有权限");
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getCode()).body(body);
    }
}

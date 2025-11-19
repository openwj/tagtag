package dev.tagtag.framework.web;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        Result<Void> body = Result.fail(ex.getErrorCode(), ex.getMessage());
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
            if (msg == null || msg.isBlank()) msg = "参数校验失败";
        } else if (ex instanceof BindException be) {
            errors = be.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            msg = String.join("; ", errors);
            if (msg == null || msg.isBlank()) msg = "参数绑定失败";
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
        if (msg == null || msg.isBlank()) msg = "参数约束违反";
        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg, errors);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
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
     * 处理权限不足异常（方法级与过滤链级）
     * @param ex 异常
     * @return 403 响应体
     */
    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<Result<Void>> handleForbidden(Exception ex) {
        Result<Void> body = Result.fail(ErrorCode.FORBIDDEN, "没有权限");
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getCode()).body(body);
    }
}

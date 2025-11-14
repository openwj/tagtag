package dev.tagtag.framework.web;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Web全局异常处理（@RestControllerAdvice）
 */
@RestControllerAdvice
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
        if (ex instanceof MethodArgumentNotValidException manv) {
            msg = manv.getBindingResult().getFieldErrors().stream()
                    .findFirst().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).orElse("参数校验失败");
        } else if (ex instanceof BindException be) {
            msg = be.getBindingResult().getFieldErrors().stream()
                    .findFirst().map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).orElse("参数绑定失败");
        } else {
            msg = "参数错误";
        }
        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
    }

    /**
     * 兜底异常处理
     * @param ex 异常
     * @return 响应体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleDefault(Exception ex) {
        Result<Void> body = Result.fail(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).body(body);
    }
}

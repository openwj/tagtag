package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = ErrorCode.BAD_REQUEST.getMessage();
        }
        Result<Void> body = Result.fail(msg);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Request body not readable: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.BAD_REQUEST, "请求体格式错误");
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    @ExceptionHandler({
        NullPointerException.class,
        IllegalStateException.class,
        RuntimeException.class
    })
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception", ex);
        Result<Void> body = Result.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).body(body);
    }
}

package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException ex) {
        int status = ex.getHttpStatus();
        String msg = ex.getMessage();

        if ((msg == null || msg.isBlank()) && ex.getErrorCode() == ErrorCode.UNAUTHORIZED) {
            msg = "未登录或会话已过期";
        }

        Result<Void> body = switch (ex.getErrorCode()) {
            case UNAUTHORIZED -> Result.unauthorized(msg);
            case FORBIDDEN -> Result.forbidden(msg);
            default -> Result.fail(ex.getErrorCode(), msg);
        };

        return ResponseEntity.status(status).body(body);
    }
}

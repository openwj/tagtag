package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<Result<Void>> handleAccessDenied(Exception ex) {
        Result<Void> body = Result.forbidden("没有权限");
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getCode()).body(body);
    }
}

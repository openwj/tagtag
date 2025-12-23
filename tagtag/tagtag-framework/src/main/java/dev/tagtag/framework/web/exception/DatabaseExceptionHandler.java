package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.warn("Database duplicate key exception: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.CONFLICT, "数据已存在，请检查唯一性约束");
        return ResponseEntity.status(ErrorCode.CONFLICT.getCode()).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation: {}", ex.getMessage());
        Result<Void> body = Result.fail(ErrorCode.CONFLICT, "数据完整性冲突，请检查数据依赖");
        return ResponseEntity.status(ErrorCode.CONFLICT.getCode()).body(body);
    }
}

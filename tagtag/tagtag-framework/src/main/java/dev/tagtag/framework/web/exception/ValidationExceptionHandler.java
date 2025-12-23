package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Result<Void>> handleValidationException(Exception ex) {
        List<String> errors = extractFieldErrors(ex);
        String msg = errors.isEmpty() ? "参数校验失败" : String.join("; ", errors);

        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg, errors);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString();
                    return path + ": " + v.getMessage();
                })
                .collect(Collectors.toList());

        String msg = errors.isEmpty() ? "参数约束违反" : String.join("; ", errors);
        Result<Void> body = Result.fail(ErrorCode.UNPROCESSABLE_ENTITY, msg, errors);
        return ResponseEntity.status(ErrorCode.UNPROCESSABLE_ENTITY.getCode()).body(body);
    }

    private List<String> extractFieldErrors(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException manv) {
            return manv.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
        } else if (ex instanceof BindException be) {
            return be.getBindingResult().getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

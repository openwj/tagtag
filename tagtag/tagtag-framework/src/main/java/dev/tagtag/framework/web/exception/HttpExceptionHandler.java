package dev.tagtag.framework.web.exception;

import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class HttpExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String msg = "不支持的方法: " + ex.getMethod();
        Result<Void> body = Result.fail(ErrorCode.METHOD_NOT_ALLOWED, msg);
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getCode()).body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String msg = "不支持的媒体类型: " + ex.getContentType();
        Result<Void> body = Result.fail(ErrorCode.UNSUPPORTED_MEDIA_TYPE, msg);
        return ResponseEntity.status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode()).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String msg = "缺少必填参数: " + ex.getParameterName();
        Result<Void> body = Result.fail(ErrorCode.BAD_REQUEST, msg);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getCode()).body(body);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        Result<Void> body = Result.fail(ErrorCode.NOT_FOUND, "资源不存在: " + ex.getRequestURL());
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getCode()).body(body);
    }
}

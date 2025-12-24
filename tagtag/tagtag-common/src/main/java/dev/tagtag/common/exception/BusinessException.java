package dev.tagtag.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 构造函数
     * @param errorCode 错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode == null ? null : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误码
     * @param message 自定义消息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误码
     * @param cause 异常原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode == null ? null : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * @param errorCode 错误码
     * @param message 自定义消息
     * @param cause 异常原因
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码数值
     * @return 错误码数值
     */
    public int getCode() {
        return errorCode == null ? -1 : errorCode.getCode();
    }

    /**
     * 获取HTTP状态码
     * @return HTTP状态码
     */
    public int getHttpStatus() {
        return errorCode == null ? 500 : errorCode.getCode();
    }

    /**
     * 基于错误码与格式化参数创建异常
     * @param errorCode 错误码枚举
     * @param args 格式化参数
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode, Object... args) {
        String msg = errorCode == null ? null : errorCode.formatMessage(args);
        return new BusinessException(errorCode, msg);
    }

    /**
     * 基于错误码创建异常
     * @param errorCode 错误码枚举
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    /**
     * 基于错误码和自定义消息创建异常
     * @param errorCode 错误码枚举
     * @param message 自定义消息
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode, String message) {
        return new BusinessException(errorCode, message);
    }

    /**
     * 基于错误码和异常原因创建异常
     * @param errorCode 错误码枚举
     * @param cause 异常原因
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode, Throwable cause) {
        return new BusinessException(errorCode, cause);
    }

    /**
     * 基于错误码、自定义消息和异常原因创建异常
     * @param errorCode 错误码枚举
     * @param message 自定义消息
     * @param cause 异常原因
     * @return 业务异常
     */
    public static BusinessException of(ErrorCode errorCode, String message, Throwable cause) {
        return new BusinessException(errorCode, message, cause);
    }

    /**
     * 创建400错误异常
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrorCode.BAD_REQUEST, message);
    }

    /**
     * 创建401错误异常
     * @return 业务异常
     */
    public static BusinessException unauthorized() {
        return new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 创建401错误异常
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, message);
    }

    /**
     * 创建403错误异常
     * @return 业务异常
     */
    public static BusinessException forbidden() {
        return new BusinessException(ErrorCode.FORBIDDEN);
    }

    /**
     * 创建403错误异常
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.FORBIDDEN, message);
    }

    /**
     * 创建404错误异常
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.NOT_FOUND, message);
    }

    /**
     * 创建500错误异常
     * @return 业务异常
     */
    public static BusinessException internalServerError() {
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 创建500错误异常，带有自定义消息
     * @param message 自定义消息
     * @return 业务异常
     */
    public static BusinessException internalServerError(String message) {
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 创建500错误异常，带有异常原因
     * @param cause 异常原因
     * @return 业务异常
     */
    public static BusinessException internalServerError(Throwable cause) {
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, cause);
    }
}

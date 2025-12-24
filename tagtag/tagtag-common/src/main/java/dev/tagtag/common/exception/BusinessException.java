package dev.tagtag.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode == null ? null : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode == null ? null : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

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
}

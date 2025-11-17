package dev.tagtag.common.model;

import dev.tagtag.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private java.util.List<String> errors;

    /**
     * 创建成功结果
     * @param data 业务数据
     * @return 成功结果
     */
    public static <T> Result<T> ok(T data) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建成功结果（无数据）
     * @return 成功结果
     */
    public static Result<Void> ok() {
        return Result.<Void>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建成功结果（仅消息）
     * @param message 成功说明
     * @return 成功结果
     */
    public static <T> Result<T> okMsg(String message) {
        return Result.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败结果（错误码默认说明）
     * @param error 错误码
     * @return 失败结果
     */
    public static <T> Result<T> fail(ErrorCode error) {
        return Result.<T>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败结果（自定义说明）
     * @param error 错误码
     * @param message 自定义失败说明
     * @return 失败结果
     */
    public static <T> Result<T> fail(ErrorCode error, String message) {
        return Result.<T>builder()
                .code(error.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败结果（自定义说明与错误列表）
     * @param error 错误码
     * @param message 失败说明
     * @param errors 错误列表
     * @return 失败结果
     */
    public static <T> Result<T> fail(ErrorCode error, String message, java.util.List<String> errors) {
        return Result.<T>builder()
                .code(error.getCode())
                .message(message)
                .errors(errors)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 判断是否成功（code == SUCCESS）
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }
}

package dev.tagtag.common.model;

import dev.tagtag.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
@Builder
public class Result<T> {
    private int code;
    private String message;
    private T data;
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
    private List<String> errors;

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
                .build();
    }

    /**
     * 创建失败结果（自定义说明与错误列表）
     * @param error 错误码
     * @param message 失败说明
     * @param errors 错误列表
     * @return 失败结果
     */
    public static <T> Result<T> fail(ErrorCode error, String message, List<String> errors) {
        return Result.<T>builder()
                .code(error.getCode())
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * 创建失败结果（仅说明，默认 BAD_REQUEST）
     * @param message 失败说明
     * @return 失败结果
     */
    public static <T> Result<T> fail(String message) {
        return Result.<T>builder()
                .code(ErrorCode.BAD_REQUEST.getCode())
                .message(message)
                .build();
    }

    /**
     * 创建禁止访问失败结果（403）
     * @param message 失败说明
     * @return 失败结果
     */
    public static <T> Result<T> forbidden(String message) {
        return Result.<T>builder()
                .code(ErrorCode.FORBIDDEN.getCode())
                .message(message)
                .build();
    }

    /**
     * 创建未认证失败结果（401）
     * @param message 失败说明
     * @return 失败结果
     */
    public static <T> Result<T> unauthorized(String message) {
        return Result.<T>builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(message)
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

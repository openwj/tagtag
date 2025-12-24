package dev.tagtag.common.exception;

import dev.tagtag.common.util.EnumUtil;
import dev.tagtag.common.enums.CodeEnum;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
public enum ErrorCode implements CodeEnum<Integer> {

    SUCCESS(200, "成功"),
    CREATED(201, "已创建"),
    NO_CONTENT(204, "无内容"),
    PARTIAL_CONTENT(206, "部分内容"),
    BAD_REQUEST(400, "请求参数错误"),
    PAYMENT_REQUIRED(402, "需要支付"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不被允许"),
    NOT_ACCEPTABLE(406, "不可接受"),
    PROXY_AUTHENTICATION_REQUIRED(407, "代理认证需要"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),
    GONE(410, "资源已不存在"),
    LENGTH_REQUIRED(411, "需要长度"),
    PRECONDITION_FAILED(412, "前置条件失败"),
    PAYLOAD_TOO_LARGE(413, "负载过大"),
    URI_TOO_LONG(414, "URI 过长"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    RANGE_NOT_SATISFIABLE(416, "范围无法满足"),
    EXPECTATION_FAILED(417, "期望失败"),
    UNPROCESSABLE_ENTITY(422, "无法处理的实体"),
    LOCKED(423, "资源被锁定"),
    FAILED_DEPENDENCY(424, "依赖失败"),
    TOO_EARLY(425, "过早"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "请求头过大"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_GATEWAY(502, "错误网关"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 使用占位参数格式化说明（String.format）
     * @param args 占位参数
     * @return 格式化后的说明
     */
    public String formatMessage(Object... args) {
        return String.format(message, args);
    }

    /**
     * 根据数值码查找枚举
     * @param code 错误码
     * @return 对应枚举，找不到返回 null
     */
    public static ErrorCode fromCode(int code) {
        return EnumUtil.fromCode(ErrorCode.class, code);
    }
}

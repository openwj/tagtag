package dev.tagtag.common.exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AssertUtils {

    /**
     * 断言对象非空，为空抛出 NullPointerException
     * @param obj 被校验对象
     * @param message 异常消息
     * @param <T> 对象类型
     * @return 原对象（便于链式调用）
     */
    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }


    /**
     * 断言字符串非空且至少包含一个非空白字符
     * @param text 字符串
     * @param message 异常消息
     * @return 原字符串
     */
    public static String hasText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }
}

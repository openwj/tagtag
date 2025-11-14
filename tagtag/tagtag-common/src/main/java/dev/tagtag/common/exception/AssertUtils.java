package dev.tagtag.common.exception;

import lombok.experimental.UtilityClass;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
     * 断言对象非空（延迟消息），为空抛出 NullPointerException
     * @param obj 被校验对象
     * @param messageSupplier 异常消息提供者
     * @param <T> 对象类型
     * @return 原对象
     */
    public static <T> T notNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new NullPointerException(messageSupplier == null ? null : messageSupplier.get());
        }
        return obj;
    }

    /**
     * 断言表达式为真，为假抛出 IllegalArgumentException
     * @param expression 布尔表达式
     * @param message 异常消息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式为假，为真抛出 IllegalArgumentException
     * @param expression 布尔表达式
     * @param message 异常消息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
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

    /**
     * 断言集合非空（非 null 且 size > 0）
     * @param collection 集合
     * @param message 异常消息
     * @param <E> 元素类型
     * @return 原集合
     */
    public static <E> Collection<E> notEmpty(Collection<E> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return collection;
    }

    /**
     * 断言 Map 非空（非 null 且 size > 0）
     * @param map 映射
     * @param message 异常消息
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 原映射
     */
    public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return map;
    }

    /**
     * 断言数组非空（非 null 且 length > 0）
     * @param array 数组
     * @param message 异常消息
     * @param <T> 元素类型
     * @return 原数组
     */
    public static <T> T[] notEmpty(T[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
        return array;
    }

    /**
     * 断言字符串最大长度不超过指定值
     * @param text 字符串
     * @param maxLength 最大长度（>=1）
     * @param message 异常消息
     * @return 原字符串
     */
    public static String maxLength(String text, int maxLength, String message) {
        Objects.requireNonNull(text, "text");
        if (maxLength < 1 || text.length() > maxLength) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

    /**
     * 断言字符串最小长度不小于指定值
     * @param text 字符串
     * @param minLength 最小长度（>=0）
     * @param message 异常消息
     * @return 原字符串
     */
    public static String minLength(String text, int minLength, String message) {
        Objects.requireNonNull(text, "text");
        if (minLength < 0 || text.length() < minLength) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

    /**
     * 断言数值为正数（>0）
     * @param number 数值
     * @param message 异常消息
     * @return 原数值
     */
    public static <N extends Number> N positive(N number, String message) {
        Objects.requireNonNull(number, "number");
        if (number.doubleValue() <= 0D) {
            throw new IllegalArgumentException(message);
        }
        return number;
    }

    /**
     * 断言数值为非负数（>=0）
     * @param number 数值
     * @param message 异常消息
     * @return 原数值
     */
    public static <N extends Number> N nonNegative(N number, String message) {
        Objects.requireNonNull(number, "number");
        if (number.doubleValue() < 0D) {
            throw new IllegalArgumentException(message);
        }
        return number;
    }

    /**
     * 断言值在闭区间 [min, max] 内
     * @param value 待校验值
     * @param min 最小值
     * @param max 最大值
     * @param message 异常消息
     * @return 原值
     */
    public static int inRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * 断言两个对象相等（Objects.equals）
     * @param expected 期望值
     * @param actual 实际值
     * @param message 异常消息
     */
    public static void equals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new IllegalArgumentException(message);
        }
    }
}

package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import java.lang.reflect.Method;

/**
 * JSON序列化/反序列化封装（Jackson）
 */
@UtilityClass
public class JsonUtils {

    private static volatile Object objectMapper;
    private static volatile Class<?> mapperClass;
    private static volatile boolean configured;

    private static Object ensureMapper() {
        if (objectMapper != null) return objectMapper;
        synchronized (JsonUtils.class) {
            if (objectMapper != null) return objectMapper;
            try {
                mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
                objectMapper = mapperClass.getDeclaredConstructor().newInstance();
                configureIfPossible(objectMapper);
                return objectMapper;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("未检测到 Jackson，请在运行环境中提供 com.fasterxml.jackson.databind.ObjectMapper 依赖", e);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Jackson ObjectMapper 初始化失败", e);
            }
        }
    }

    /**
     * 尝试进行基础配置（JavaTimeModule、禁用时间戳、忽略空值等），若相关类不存在则跳过
     * @param mapper ObjectMapper实例
     */
    private static void configureIfPossible(Object mapper) {
        if (configured) return;
        try {
            Class<?> serializationFeature = Class.forName("com.fasterxml.jackson.databind.SerializationFeature");
            Class<?> jsonInclude = Class.forName("com.fasterxml.jackson.annotation.JsonInclude$Include");
            Class<?> javaTimeModule = Class.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");

            Method disable = mapperClass.getMethod("disable", serializationFeature);
            Method setInclusion = mapperClass.getMethod("setSerializationInclusion", jsonInclude);
            Method registerModule = mapperClass.getMethod("registerModule", Class.forName("com.fasterxml.jackson.databind.Module"));

            Object WRITE_DATES_AS_TIMESTAMPS = serializationFeature.getField("WRITE_DATES_AS_TIMESTAMPS").get(null);
            Object NON_NULL = jsonInclude.getField("NON_NULL").get(null);
            Object jtm = javaTimeModule.getDeclaredConstructor().newInstance();

            disable.invoke(mapper, WRITE_DATES_AS_TIMESTAMPS);
            setInclusion.invoke(mapper, NON_NULL);
            registerModule.invoke(mapper, jtm);
            configured = true;
        } catch (Throwable ignore) {
            // 若环境未引入 jackson-datatype-jsr310 或相关类，直接跳过配置
        }
    }

    /**
     * 对象转 JSON 字符串
     * @param value 对象
     * @return JSON 字符串
     */
    public static String toJson(Object value) {
        try {
            Object mapper = ensureMapper();
            Method write = mapperClass.getMethod("writeValueAsString", Object.class);
            return (String) write.invoke(mapper, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * JSON 字符串转对象
     * @param json 文本
     * @param clazz 目标类型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            Object mapper = ensureMapper();
            Method read = mapperClass.getMethod("readValue", String.class, Class.class);
            return (T) read.invoke(mapper, json, clazz);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * 对象转 JSON 字节
     * @param value 对象
     * @return JSON 字节数组
     */
    public static byte[] toJsonBytes(Object value) {
        try {
            Object mapper = ensureMapper();
            Method write = mapperClass.getMethod("writeValueAsBytes", Object.class);
            return (byte[]) write.invoke(mapper, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * JSON 字节转对象
     * @param bytes JSON字节数组
     * @param clazz 目标类型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJsonBytes(byte[] bytes, Class<T> clazz) {
        try {
            Object mapper = ensureMapper();
            Method read = mapperClass.getMethod("readValue", byte[].class, Class.class);
            return (T) read.invoke(mapper, bytes, clazz);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
}

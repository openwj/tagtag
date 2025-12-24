package dev.tagtag.common.enums;

/**
 * 带码值枚举接口
 * @param <T> 码值类型
 */
public interface CodeEnum<T> {

    /**
     * 获取码值
     * @return 码值
     */
    T getCode();
}
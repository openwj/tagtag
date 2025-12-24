package dev.tagtag.common.util;

import dev.tagtag.common.enums.CodeEnum;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

/**
 * 枚举工具类
 */
@UtilityClass
public class EnumUtil {

    /**
     * 根据码值获取枚举实例
     * @param enumType 枚举类型
     * @param code 码值
     * @param <E> 枚举类型
     * @param <T> 码值类型
     * @return 枚举实例，不存在返回null
     */
    public static <E extends Enum<E> & CodeEnum<T>, T> E fromCode(Class<E> enumType, T code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> code.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据码值获取枚举实例，返回Optional
     * @param enumType 枚举类型
     * @param code 码值
     * @param <E> 枚举类型
     * @param <T> 码值类型
     * @return Optional包装的枚举实例
     */
    public static <E extends Enum<E> & CodeEnum<T>, T> Optional<E> fromCodeOptional(Class<E> enumType, T code) {
        return Optional.ofNullable(fromCode(enumType, code));
    }

    /**
     * 检查码值是否有效
     * @param enumType 枚举类型
     * @param code 码值
     * @param <E> 枚举类型
     * @param <T> 码值类型
     * @return 码值是否有效
     */
    public static <E extends Enum<E> & CodeEnum<T>, T> boolean isValidCode(Class<E> enumType, T code) {
        return fromCode(enumType, code) != null;
    }
}
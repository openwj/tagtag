package dev.tagtag.framework.web.converter;

import dev.tagtag.common.enums.CodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 将字符串转换为实现了 CodeEnum 接口的枚举类型的通用转换工厂。
 * 支持数字编码与枚举名称（不区分大小写）。
 */
public class StringToCodeEnumConverterFactory implements ConverterFactory<String, Enum> {

    /**
     * 获取指定枚举类型的字符串转换器
     * @param targetType 目标枚举类型
     * @return 对应的字符串到枚举的转换器
     */
    @Override
    public <T extends Enum<T>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToCodeEnumConverter<>(targetType);
    }

    /**
     * 字符串到实现了 CodeEnum 的枚举类型的具体转换器
     * @param <T> 目标枚举类型参数
     */
    private static class StringToCodeEnumConverter<T extends Enum<T>> implements Converter<String, T> {

        private final Class<T> enumType;

        /**
         * 构造函数
         * @param enumType 目标枚举类型
         */
        public StringToCodeEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        /**
         * 执行转换逻辑
         * @param source 输入字符串（查询参数）
         * @return 转换后的枚举值；无法识别返回 null
         */
        @Override
        public T convert(String source) {
            if (source == null) return null;
            String s = source.trim();
            if (s.isEmpty()) return null;

            // 若枚举实现了 CodeEnum，则优先按数字编码匹配
            boolean supportsCode = CodeEnum.class.isAssignableFrom(enumType);
            if (supportsCode) {
                try {
                    int code = Integer.parseInt(s);
                    for (T constant : enumType.getEnumConstants()) {
                        CodeEnum ce = (CodeEnum) constant;
                        if (ce.getCode() == code) return constant;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            // 回退按名称（大小写不敏感）匹配
            try {
                @SuppressWarnings("unchecked")
                T byName = (T) Enum.valueOf(enumType, s.toUpperCase());
                return byName;
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
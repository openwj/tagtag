package dev.tagtag.framework.web.converter;

import dev.tagtag.common.enums.CodeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 将字符串转换为实现了 CodeEnum 接口的枚举类型的通用转换工厂。
 * 支持数字编码与枚举名称（不区分大小写）。
 */
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    /**
     * 获取指定枚举类型的字符串转换器
     * @param targetType 目标枚举类型
     * @return 对应的字符串到枚举的转换器
     */
    @Override
    public @NonNull <T extends Enum<?>> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    /**
     * 字符串到实现了 CodeEnum 的枚举类型的具体转换器
     * @param <T> 目标枚举类型参数
     */
    private static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {
        private final boolean supportsCode;
        private final Map<String, T> nameMap;
        private final Map<Integer, T> codeMap;

        /**
         * 构造函数
         * @param enumType 目标枚举类型
         */
        public StringToEnumConverter(Class<T> enumType) {
            this.supportsCode = CodeEnum.class.isAssignableFrom(enumType);
            this.nameMap = new HashMap<>();
            this.codeMap = this.supportsCode ? new HashMap<>() : null;

            for (T constant : enumType.getEnumConstants()) {
                String key = normalizeName(constant.name());
                nameMap.put(key, constant);
                if (supportsCode) {
                    CodeEnum ce = (CodeEnum) constant;
                    codeMap.put(ce.getCode(), constant);
                }
            }
        }

        /**
         * 执行转换逻辑
         * @param source 输入字符串（查询参数）
         * @return 转换后的枚举值；无法识别返回 null
         */
        @Override
        public @Nullable T convert(String source) {
            String s = source.trim();
            if (s.isEmpty()) return null;
            if ("null".equalsIgnoreCase(s) || "undefined".equalsIgnoreCase(s)) return null;

            if (supportsCode) {
                try {
                    int code = Integer.parseInt(s);
                    T byCode = codeMap.get(code);
                    if (byCode != null) return byCode;
                } catch (NumberFormatException ignored) {
                }
            }

            String key = normalizeName(s);
            return nameMap.getOrDefault(key, null);
        }

        /**
         * 名称规范化：去空白、转大写、连字符与空格统一为下划线
         * @param name 原始名称
         * @return 规范化后的名称键
         */
        private String normalizeName(String name) {
            String n = name.trim().toUpperCase();
            n = n.replace('-', '_').replace(' ', '_');
            return n;
        }
    }
}
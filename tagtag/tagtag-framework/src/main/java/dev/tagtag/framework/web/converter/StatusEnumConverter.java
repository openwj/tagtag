package dev.tagtag.framework.web.converter;

import dev.tagtag.common.enums.StatusEnum;
import org.springframework.core.convert.converter.Converter;

/**
 * 将请求参数中的字符串转换为 StatusEnum。
 * 支持数字编码（如 "0"、"1"）与枚举名称（如 "DISABLED"、"ENABLED"，不区分大小写）。
 */
public class StatusEnumConverter implements Converter<String, StatusEnum> {

    /**
     * 执行字符串到 StatusEnum 的转换
     * @param source 查询参数字符串，如 "0"、"1"、"DISABLED"、"ENABLED"
     * @return 对应的 StatusEnum；无法识别返回 null（保留字段为空的语义）
     */
    @Override
    public StatusEnum convert(String source) {
        if (source == null) return null;
        String s = source.trim();
        if (s.isEmpty()) return null;
        try {
            Integer code = Integer.valueOf(s);
            StatusEnum byCode = StatusEnum.fromCode(code);
            if (byCode != null) return byCode;
        } catch (NumberFormatException ignored) {
        }
        try {
            return StatusEnum.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
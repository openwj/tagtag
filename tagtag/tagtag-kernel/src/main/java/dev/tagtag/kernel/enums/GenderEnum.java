package dev.tagtag.kernel.enums;

import dev.tagtag.common.util.EnumUtil;
import dev.tagtag.common.enums.CodeEnum;
import lombok.Getter;

/**
 * 性别枚举
 */
@Getter
public enum GenderEnum implements CodeEnum<Integer> {

    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private final Integer code;

    GenderEnum(Integer code) {
        this.code = code;
    }

    /**
     * 根据码值获取枚举实例
     * @param code 码值
     * @return 枚举实例，不存在返回null
     */
    public static GenderEnum fromCode(Integer code) {
        return EnumUtil.fromCode(GenderEnum.class, code);
    }
}

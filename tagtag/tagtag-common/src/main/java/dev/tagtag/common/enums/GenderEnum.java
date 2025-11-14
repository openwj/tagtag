package dev.tagtag.common.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {

    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private final int code;

    GenderEnum(int code) {
        this.code = code;
    }

    /**
     * 根据整型编码转换为枚举
     *
     * @param code 性别编码
     * @return 对应的枚举，无法识别则返回 null
     */
    public static GenderEnum fromCode(Integer code) {
        if (code == null) return null;
        for (GenderEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}


package dev.tagtag.common.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {

    DISABLED(0),
    ENABLED(1);

    private final int code;

    StatusEnum(int code) {
        this.code = code;
    }

    /**
     * 根据整型编码转换为枚举
     *
     * @param code 状态编码
     * @return 对应的枚举，无法识别则返回 null
     */
    public static StatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (StatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}


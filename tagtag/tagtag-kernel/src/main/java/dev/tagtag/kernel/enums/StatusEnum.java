package dev.tagtag.kernel.enums;

import lombok.Getter;

@Getter
public enum StatusEnum implements CodeEnum {

    DISABLED(0),
    ENABLED(1);

    private final int code;

    StatusEnum(int code) {
        this.code = code;
    }

    public static StatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (StatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}

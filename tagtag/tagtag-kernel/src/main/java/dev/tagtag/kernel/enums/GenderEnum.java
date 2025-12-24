package dev.tagtag.kernel.enums;

import lombok.Getter;

@Getter
public enum GenderEnum implements CodeEnum {

    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private final int code;

    GenderEnum(int code) {
        this.code = code;
    }

    public static GenderEnum fromCode(Integer code) {
        if (code == null) return null;
        for (GenderEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}

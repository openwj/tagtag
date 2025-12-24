package dev.tagtag.kernel.enums;

import dev.tagtag.common.util.EnumUtil;
import dev.tagtag.common.enums.CodeEnum;
import lombok.Getter;

@Getter
public enum StatusEnum implements CodeEnum<Integer> {

    DISABLED(0),
    ENABLED(1);

    private final Integer code;

    StatusEnum(Integer code) {
        this.code = code;
    }

    public static StatusEnum fromCode(Integer code) {
        return EnumUtil.fromCode(StatusEnum.class, code);
    }
}

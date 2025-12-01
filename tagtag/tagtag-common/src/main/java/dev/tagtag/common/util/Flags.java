package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

/**
 * 整型布尔标记工具：统一将 0/1/NULL 转换为布尔值
 */
@UtilityClass
public class Flags {

    /**
     * 判断整型标记是否为真
     * 仅当 flag 非空且等于 1 时返回 true；其余（0/NULL/其他值）返回 false
     * @param flag 整型布尔标记（0/1/NULL）
     * @return 布尔结果
     */
    public static boolean isTrue(Integer flag) {
        return flag != null && flag == 1;
    }
}


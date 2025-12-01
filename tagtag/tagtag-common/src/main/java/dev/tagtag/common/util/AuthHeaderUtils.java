package dev.tagtag.common.util;

import dev.tagtag.common.constant.GlobalConstants;
import lombok.experimental.UtilityClass;

/**
 * 认证头工具：统一解析 Authorization: Bearer <token>
 */
@UtilityClass
public class AuthHeaderUtils {

    /**
     * 解析 Authorization 头中的 Bearer 令牌
     * @param authorization Authorization 头字符串，可能为 null
     * @return 裸 Token；若为空或无效返回 null
     */
    public static String parseBearerToken(String authorization) {
        if (authorization == null) return null;
        String v = authorization.trim();
        if (v.isEmpty()) return null;
        if ("Bearer".equals(v)) return null;
        if (v.startsWith(GlobalConstants.TOKEN_PREFIX)) {
            v = v.substring(GlobalConstants.TOKEN_PREFIX.length()).trim();
        }
        return v.isEmpty() ? null : v;
    }
}

package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import dev.tagtag.common.constant.GlobalConstants;
import java.util.Map;

/**
 * HTTP请求信息获取工具（IP、UA等）
 */
@UtilityClass
public class RequestUtils {

    /**
     * 从请求头中提取客户端 IP（优先 X-Forwarded-For、X-Real-IP）
     * @param headers 请求头键值对
     * @param remoteAddr 远端地址回退值
     * @return 客户端 IP
     */
    public static String extractClientIp(Map<String, String> headers, String remoteAddr) {
        if (headers != null) {
            String xff = headers.get(GlobalConstants.HEADER_FORWARDED);
            if (xff != null && !xff.isBlank()) {
                String first = xff.split(",")[0].trim();
                if (!first.isEmpty()) return first;
            }
            String realIp = headers.get(GlobalConstants.HEADER_REAL_IP);
            if (realIp != null && !realIp.isBlank()) {
                return realIp.trim();
            }
        }
        return remoteAddr;
    }

    /**
     * 提取 User-Agent
     * @param headers 请求头键值对
     * @return UA 文本
     */
    public static String extractUserAgent(Map<String, String> headers) {
        if (headers == null) return null;
        String ua = headers.get(GlobalConstants.HEADER_USER_AGENT);
        return ua == null ? null : ua.trim();
    }

    /**
     * 判断是否 Ajax 请求（X-Requested-With = XMLHttpRequest）
     * @param headers 请求头
     * @return 是否为 Ajax
     */
    public static boolean isAjax(Map<String, String> headers) {
        if (headers == null) return false;
        String xrw = headers.get("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(xrw);
    }
}



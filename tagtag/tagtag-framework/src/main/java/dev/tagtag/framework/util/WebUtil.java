package dev.tagtag.framework.util;

import dev.tagtag.common.constant.GlobalConstants;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Web 工具类：提供 IP 解析、UA 获取等 Web 相关工具方法
 */
@UtilityClass
public class WebUtil {
    
    /**
     * 解析客户端 IP（优先使用反向代理头部）
     * @return 客户端 IP
     */
    public static String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            
            // 优先使用 X-Forwarded-For 头部
            String xff = req.getHeader(GlobalConstants.HEADER_FORWARDED);
            if (isNotEmpty(xff)) {
                int idx = xff.indexOf(',');
                return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
            }
            
            // 其次使用 X-Real-IP 头部
            String rip = req.getHeader(GlobalConstants.HEADER_REAL_IP);
            if (isNotEmpty(rip)) return rip.trim();
            
            // 最后使用默认的 remoteAddr
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 读取客户端 User-Agent
     * @return UA 字符串
     */
    public static String getUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String ua = req.getHeader(GlobalConstants.HEADER_USER_AGENT);
            return isNotEmpty(ua) ? ua.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查字符串是否非空
     * 
     * @param str 字符串
     * @return 是否非空
     */
    private static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
package dev.tagtag.framework.util;

import dev.tagtag.framework.constant.RateLimitConstants;
import dev.tagtag.framework.constant.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class IpUtils {

    private IpUtils() {}

    public static String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader(SecurityConstants.X_FORWARDED_FOR);
            if (StringUtils.hasText(xff)) {
                int idx = xff.indexOf(',');
                return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
            }
            String rip = req.getHeader(SecurityConstants.X_REAL_IP);
            if (StringUtils.hasText(rip)) {
                return rip.trim();
            }
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
}

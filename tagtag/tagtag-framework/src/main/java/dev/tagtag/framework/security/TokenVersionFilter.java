package dev.tagtag.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.util.Numbers;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import dev.tagtag.kernel.constant.SecurityClaims;

/**
 * 令牌版本校验过滤器：当请求携带 Bearer Token 时，校验其 claims 中的 ver 是否为用户当前版本。
 * 若不匹配，直接返回 401 并输出统一错误结构，避免旧令牌继续访问。
 */
@Component
public class TokenVersionFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenVersionService tokenVersionService;
    private final ObjectMapper objectMapper;

    /**
     * 构造函数：注入 JWT 服务、令牌版本服务与全局 ObjectMapper
     * @param jwtService JWT 服务
     * @param tokenVersionService 令牌版本服务
     * @param objectMapper 全局 ObjectMapper（统一序列化配置）
     */
    public TokenVersionFilter(JwtService jwtService, TokenVersionService tokenVersionService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.tokenVersionService = tokenVersionService;
        this.objectMapper = objectMapper;
    }

    /**
     * 过滤执行：当请求有 Bearer 令牌时校验 ver 与当前版本一致，否则返回 401
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length()).trim();
            if (!jwtService.validateToken(token)) {
                writeUnauthorized(response);
                return;
            }
            Map<String, Object> claims = jwtService.getClaims(token);
            Object uidObj = claims.get(SecurityClaims.UID);
            Object verObj = claims.get(SecurityClaims.VER);
            Long uid = uidObj == null ? null : Numbers.toLong(uidObj);
            Long ver = verObj == null ? null : Numbers.toLong(verObj);
            if (uid != null && ver != null) {
                if (!tokenVersionService.isTokenVersionValid(uid, ver)) {
                    writeUnauthorized(response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    

    /**
     * 输出统一未认证响应（401）
     * @param response HTTP 响应
     * @throws IOException IO 异常
     */
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(ErrorCode.UNAUTHORIZED.getCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Result<Void> body = Result.fail(ErrorCode.UNAUTHORIZED, "凭证无效");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
package dev.tagtag.framework.security;

import dev.tagtag.kernel.constant.Roles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 方法级角色守卫：拦截带有 @RequireRole 的方法并校验角色
 */
@Aspect
@Component
public class RoleGuard {
    /**
     * 拦截并校验角色：将业务角色码拼接前缀后与当前认证的权限集合比对
     * @param pjp 连接点
     * @param rr 注解实例，包含业务角色码
     * @return 方法执行结果
     */
    @Around("@annotation(rr)")
    public Object around(ProceedingJoinPoint pjp, RequireRole rr) throws Throwable {
        String required = Roles.PREFIX + rr.value();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("未认证");
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean match = false;
        if (authorities != null) {
            for (GrantedAuthority ga : authorities) {
                if (ga != null && required.equals(ga.getAuthority())) { match = true; break; }
            }
        }
        if (!match) throw new AccessDeniedException("角色不足: " + required);
        return pjp.proceed();
    }
}


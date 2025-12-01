package dev.tagtag.framework.security;

import dev.tagtag.kernel.constant.Permissions;
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
 * 方法级权限守卫：拦截带有 @RequirePerm 的方法并校验权限
 */
@Aspect
@Component
public class PermissionGuard {
    /**
     * 拦截并校验权限：将业务码拼接前缀后与当前认证的权限集合比对
     * @param pjp 连接点
     * @param rp 注解实例，包含业务权限码
     * @return 方法执行结果
     */
    @Around("@annotation(rp)")
    public Object around(ProceedingJoinPoint pjp, RequirePerm rp) throws Throwable {
        String required = Permissions.PREFIX + rp.value();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("未认证");
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        boolean match = false;
        if (authorities != null) {
            for (GrantedAuthority ga : authorities) {
                if (ga != null && required.equals(ga.getAuthority())) { match = true; break; }
            }
        }
        if (!match) throw new AccessDeniedException("权限不足: " + required);
        return pjp.proceed();
    }
}


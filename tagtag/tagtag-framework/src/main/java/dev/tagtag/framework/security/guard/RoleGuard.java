package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.annotation.RequireRole;
import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.framework.security.util.SecurityUtils;
import dev.tagtag.kernel.constant.Roles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleGuard {

    @Around("@annotation(rr)")
    public Object around(ProceedingJoinPoint pjp, RequireRole rr) throws Throwable {
        UserPrincipal principal = AuthContext.getCurrentPrincipal();

        if (principal.isAdmin()) {
            return pjp.proceed();
        }

        String required = Roles.PREFIX + rr.value();
        if (!SecurityUtils.hasAuthority(required)) {
            throw new AccessDeniedException("角色不足: " + required);
        }
        return pjp.proceed();
    }
}

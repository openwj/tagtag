package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.annotation.RequirePerm;
import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.framework.security.util.SecurityUtils;
import dev.tagtag.kernel.constant.Permissions;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionGuard {

    @Around("@annotation(rp)")
    public Object around(ProceedingJoinPoint pjp, RequirePerm rp) throws Throwable {
        UserPrincipal principal = AuthContext.getCurrentPrincipal();

        if (principal.isAdmin()) {
            return pjp.proceed();
        }

        String required = Permissions.authority(rp.value());
        if (!SecurityUtils.hasAuthority(required)) {
            throw new AccessDeniedException("权限不足: " + required);
        }
        return pjp.proceed();
    }
}

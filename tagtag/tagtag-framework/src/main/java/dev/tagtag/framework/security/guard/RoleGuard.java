package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.annotation.RequireRole;
import dev.tagtag.framework.constant.Roles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleGuard extends AbstractAuthorityGuard {

    @Around("@annotation(requireRole)")
    public Object around(ProceedingJoinPoint pjp, RequireRole requireRole) throws Throwable {
        return doGuard(pjp, requireRole);
    }

    @Override
    protected String getRequiredAuthority(Object annotation) {
        RequireRole rr = (RequireRole) annotation;
        return Roles.PREFIX + rr.value();
    }

    @Override
    protected String getAuthorityType() {
        return "角色";
    }
}

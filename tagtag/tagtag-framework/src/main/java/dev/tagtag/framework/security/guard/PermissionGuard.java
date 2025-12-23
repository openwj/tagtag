package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.annotation.RequirePerm;
import dev.tagtag.framework.constant.Permissions;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionGuard extends AbstractAuthorityGuard {

    @Around("@annotation(requirePerm)")
    public Object around(ProceedingJoinPoint pjp, RequirePerm requirePerm) throws Throwable {
        return doGuard(pjp, requirePerm);
    }

    @Override
    protected String getRequiredAuthority(Object annotation) {
        RequirePerm rp = (RequirePerm) annotation;
        return Permissions.PREFIX + rp.value();
    }

    @Override
    protected String getAuthorityType() {
        return "权限";
    }
}

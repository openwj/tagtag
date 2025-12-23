package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.framework.security.util.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public abstract class AbstractAuthorityGuard {

    protected abstract String getRequiredAuthority(Object annotation);

    protected abstract String getAuthorityType();

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, Object annotation) throws Throwable {
        UserPrincipal principal = AuthContext.getCurrentPrincipal();

        if (principal.isAdmin()) {
            return pjp.proceed();
        }

        String required = getRequiredAuthority(annotation);
        if (!SecurityUtils.hasAuthority(required)) {
            throw new AccessDeniedException(getAuthorityType() + "不足: " + required);
        }
        return pjp.proceed();
    }
}

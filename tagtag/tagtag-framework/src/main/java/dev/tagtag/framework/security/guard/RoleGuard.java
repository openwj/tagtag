package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.constant.Roles;
import dev.tagtag.framework.security.annotation.RequireRole;
import org.springframework.stereotype.Component;

@Component
public class RoleGuard extends AbstractAuthorityGuard {

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

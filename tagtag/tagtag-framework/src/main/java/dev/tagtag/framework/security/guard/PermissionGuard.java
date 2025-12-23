package dev.tagtag.framework.security.guard;

import dev.tagtag.framework.security.annotation.RequirePerm;
import dev.tagtag.framework.constant.Permissions;
import org.springframework.stereotype.Component;

@Component
public class PermissionGuard extends AbstractAuthorityGuard {

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

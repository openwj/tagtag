package dev.tagtag.framework.config;

import dev.tagtag.kernel.constant.SecurityConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private List<String> permitPaths = new ArrayList<>();

    public List<String> getEffectivePermitPaths() {
        List<String> paths = new ArrayList<>(SecurityConstants.DEFAULT_PUBLIC_PATHS);
        if (permitPaths != null && !permitPaths.isEmpty()) {
            paths.addAll(permitPaths);
        }
        return paths;
    }
}

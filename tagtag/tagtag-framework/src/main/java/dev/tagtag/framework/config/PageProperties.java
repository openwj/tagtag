package dev.tagtag.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

import java.util.Set;

/**
 * 分页排序白名单配置
 */
@Component
@ConfigurationProperties(prefix = "page.whitelist")
@Data
public class PageProperties {

    private int defaultPageSize = 20;
    private int maxPageSize = 200;
    private Set<String> dept = Set.of("id", "name", "sort", "createTime", "create_time");
    private Set<String> user = Set.of("id", "username", "nickname", "createTime", "create_time");
    private Set<String> role = Set.of("id", "code", "name", "createTime", "create_time");
    private Set<String> menu = Set.of("id", "menuCode", "menuName", "createTime", "create_time", "sort");
}
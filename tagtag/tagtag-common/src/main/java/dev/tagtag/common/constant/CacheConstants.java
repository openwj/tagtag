package dev.tagtag.common.constant;

import lombok.experimental.UtilityClass;
import java.time.Duration;
import java.util.Objects;

@UtilityClass
public class CacheConstants {

    public static final String SEPARATOR = ":";
    public static final String PREFIX = "tagtag";
    public static final String AUTH = "auth";
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String PERM = "perm";
    public static final String DEPT = "dept";
    public static final String CONFIG = "config";
    public static final String CAPTCHA = "captcha";
    public static final String RATE_LIMIT = "rate-limit";
    public static final String DICT = "dict";
    public static final String MENU = "menu";

    public static final Duration TOKEN_TTL = Duration.ofHours(12);
    public static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);
    public static final Duration USER_INFO_TTL = Duration.ofMinutes(30);
    public static final Duration PERMISSIONS_TTL = Duration.ofMinutes(10);
    public static final Duration CONFIG_TTL = Duration.ofMinutes(10);
    public static final Duration RATE_LIMIT_TTL = Duration.ofMinutes(1);
    public static final Duration MENU_TTL = Duration.ofMinutes(30);
    public static final Duration DICT_TTL = Duration.ofHours(1);
    public static final Duration DEPT_TREE_TTL = Duration.ofMinutes(30);

    /**
     * 构建 Redis Key（统一使用冒号拼接）
     * @param segments key 段
     * @return 规范化 key
     */
    public static String compose(String... segments) {
        Objects.requireNonNull(segments, "segments");
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            String s = Objects.requireNonNull(segment, "segment").trim();
            if (s.isEmpty()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(SEPARATOR);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 构建登录令牌缓存Key
     * @param token 登录令牌
     * @return 缓存Key
     */
    public static String keyAuthToken(String token) {
        Objects.requireNonNull(token, "token");
        return compose(PREFIX, AUTH, "token", token);
    }

    /**
     * 构建用户信息缓存Key
     * @param userId 用户ID
     * @return 缓存Key
     */
    public static String keyUserInfo(Long userId) {
        Objects.requireNonNull(userId, "userId");
        return compose(PREFIX, USER, "info", String.valueOf(userId));
    }

    /**
     * 构建用户权限列表缓存Key
     * @param userId 用户ID
     * @return 缓存Key
     */
    public static String keyUserPermissions(Long userId) {
        Objects.requireNonNull(userId, "userId");
        return compose(PREFIX, PERM, "list", String.valueOf(userId));
    }

    /**
     * 构建角色信息缓存Key
     * @param roleId 角色ID
     * @return 缓存Key
     */
    public static String keyRoleInfo(Long roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return compose(PREFIX, ROLE, "info", String.valueOf(roleId));
    }

    /**
     * 构建角色权限列表缓存Key
     * @param roleId 角色ID
     * @return 缓存Key
     */
    public static String keyRolePermissions(Long roleId) {
        Objects.requireNonNull(roleId, "roleId");
        return compose(PREFIX, ROLE, "perm", "list", String.valueOf(roleId));
    }

    /**
     * 构建部门树缓存Key
     * @return 缓存Key
     */
    public static String keyDeptTree() {
        return compose(PREFIX, DEPT, "tree");
    }

    /**
     * 构建系统配置项缓存Key
     * @param configKey 配置项Key
     * @return 缓存Key
     */
    public static String keySystemConfig(String configKey) {
        Objects.requireNonNull(configKey, "configKey");
        String key = configKey.trim();
        return compose(PREFIX, CONFIG, "item", key);
    }

    /**
     * 构建图形验证码缓存Key
     * @param uuid 验证码唯一标识
     * @return 缓存Key
     */
    public static String keyCaptcha(String uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return compose(PREFIX, CAPTCHA, "img", uuid.trim());
    }

    /**
     * 构建限流计数缓存Key
     * @param api 接口标识
     * @param ip 访问IP
     * @return 缓存Key
     */
    public static String keyRateLimit(String api, String ip) {
        Objects.requireNonNull(api, "api");
        Objects.requireNonNull(ip, "ip");
        return compose(PREFIX, RATE_LIMIT, api.trim(), ip.trim());
    }

    /**
     * 构建字典类型缓存Key
     * @param typeCode 字典类型编码
     * @return 缓存Key
     */
    public static String keyDictType(String typeCode) {
        Objects.requireNonNull(typeCode, "typeCode");
        return compose(PREFIX, DICT, "type", typeCode.trim());
    }

    /**
     * 构建菜单树缓存Key（按用户维度）
     * @param userId 用户ID
     * @return 缓存Key
     */
    public static String keyMenuTree(Long userId) {
        Objects.requireNonNull(userId, "userId");
        return compose(PREFIX, MENU, "tree", String.valueOf(userId));
    }
}

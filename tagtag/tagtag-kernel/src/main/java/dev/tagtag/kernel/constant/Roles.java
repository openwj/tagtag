package dev.tagtag.kernel.constant;

public final class Roles {

    private Roles() {}

    public static final String PREFIX = "ROLE_";

    public static final String ADMIN = "admin";
    public static final String USER = "user";

    public static String authority(String role) {
        return PREFIX + role;
    }
}

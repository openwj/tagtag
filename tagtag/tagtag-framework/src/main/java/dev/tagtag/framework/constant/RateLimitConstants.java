package dev.tagtag.framework.constant;

public final class RateLimitConstants {

    private RateLimitConstants() {}

    public static final String RATE_LIMIT_SCRIPT =
            "local current = redis.call('INCR', KEYS[1]);" +
            "if current == 1 then redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1])); end;" +
            "if current > tonumber(ARGV[2]) then return 0 else return 1 end";

    public static final int MIN_PERIOD_SECONDS = 1;
    public static final int MIN_PERMITS = 1;
    public static final String UNKNOWN_IP = "unknown";
}

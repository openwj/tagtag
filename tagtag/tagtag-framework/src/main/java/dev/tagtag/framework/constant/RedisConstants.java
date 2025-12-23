package dev.tagtag.framework.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisConstants {

    public static final String KEY_SERIALIZER_TYPE = "String";
    public static final String VALUE_SERIALIZER_TYPE = "GenericJackson2Json";
    public static final String REDIS_KEY_PREFIX = "tagtag:";
    public static final String REDIS_CACHE_PREFIX = "cache:";
    public static final String REDIS_LOCK_PREFIX = "lock:";
}

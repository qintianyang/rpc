package com.kk.constants;

public class RpcConstants {
    // 魔数
    public static final short MAGIC_NUMBER = 9527;
    /**
     * redis服务前缀
     */
    public static final String REDIS_SERVICE_PREFIX_KEY = "rpc:service";
    /**
     * redis服务前缀默认过期时间
     */
    public static final int REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION =  60 * 30 * 10;
    /**
     * redis客户端前缀过期时间
     */
    public static final int REDIS_CLIENT_PREFIX_DEFAULT_EXPIRATION = 30;
}

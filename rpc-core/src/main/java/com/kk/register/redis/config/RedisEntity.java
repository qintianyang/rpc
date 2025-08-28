package com.kk.register.redis.config;

import lombok.Data;


@Data
public class RedisEntity {
    private String host;
    private String password;
    private int port;
}

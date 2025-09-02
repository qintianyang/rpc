package com.kk.cache;

import com.kk.config.ServerConfig;
import com.kk.register.Register;
import com.kk.serializer.Serializer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCache {
    public static Serializer serializer;
    public static Register register;
    public static ServerConfig serverConfig;
    public static Map<String, Object> services = new ConcurrentHashMap<>();
}
package com.kk.config;

import cn.hutool.core.util.ObjectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件加载工具类，用于加载和读取 .properties 文件。
 */
public class PropertiesLoader {
    // 存储配置文件内容
    private static Properties properties;

    // 缓存配置项，减少重复读取
    private static Map<String, String> propertiesMap = new HashMap<>();

    // 默认配置文件名
    private static String DEFAULT_PROPERTIES_FILE = "rpc.properties";

    /**
     * 加载配置文件。如果已经加载过，则直接返回。
     * @throws IOException 如果配置文件不存在或读取失败
     */
    public static void loadConfiguration() throws IOException {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        try (InputStream in = PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE)) {
            if (in == null) {
                throw new IOException("rpc properties file" + DEFAULT_PROPERTIES_FILE + " not found");
            }
            properties.load(in);
        }
    }

    /**
     * 根据键获取字符串类型的值。
     * @param key 配置项的键
     * @return 配置项的值，如果不存在则返回 null
     */
    public static String getPropertiesStr(String key) {
        if (properties == null) {
            return null;
        }
        if (ObjectUtil.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String val = properties.getProperty(key);
            propertiesMap.put(key, val);
        }
        return propertiesMap.get(key);
    }

    /**
     * 根据键获取字符串类型的值，如果不存在则返回默认值。
     * @param key 配置项的键
     * @param defaultVal 默认值
     * @return 配置项的值或默认值
     */
    public static String getPropertiesStrDefault(String key, String defaultVal) {
        String val = getPropertiesStr(key);
        return val == null || val.equals("") ? defaultVal : val;
    }

    /**
     * 根据键获取整数类型的值。
     * @param key 配置项的键
     * @return 配置项的值（整数），如果不存在则返回 null
     */
    public static Integer getPropertiesInteger(String key) {
        if (properties == null) {
            return null;
        }
        if (ObjectUtil.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }

    /**
     * 根据键获取整数类型的值，如果不存在则返回默认值。
     * @param key 配置项的键
     * @param defaultVal 默认值
     * @return 配置项的值或默认值
     */
    public static Integer getPropertiesIntegerDefault(String key, Integer defaultVal) {
        if (properties == null) {
            return defaultVal;
        }
        if (ObjectUtil.isEmpty(key)) {
            return defaultVal;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            propertiesMap.put(key, String.valueOf(defaultVal));
            return defaultVal;
        }
        if (!propertiesMap.containsKey(key)) {
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }
}
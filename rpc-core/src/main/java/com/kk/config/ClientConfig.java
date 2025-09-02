package com.kk.config;

import lombok.Data;

@Data
public class ClientConfig {
    // 路由类型
    private String routerType;
    // 应用名
    private String applicationName;
    // 注册中心地址
    private String registerAddress;
    // 序列化类型
    private String clientSerialize;
    // 注册中心类型
    private String registerType;
    // 重试次数
    private int retryTimes;
    // 重试间隔
    private int retryInterval;
    // 注册中心密码
    private String registerPassword;
}
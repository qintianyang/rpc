package com.kk.config;

import lombok.Data;

@Data
public class ServerConfig {
    // 服务端口
    private int serverPort;
    // 注册中心地址
    private String registerAddress;
    // 注册中心类型
    private String registerType;
    // 路由策略
    private String applicationName;
    // 序列化方式
    private String serverSerialize;
    // 注册中心密码
    private String registerPassword;
}
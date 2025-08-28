package com.kk.register;

import com.kk.util.ServiceWrapper;

import java.util.List;

public interface Register {
    /**
     * 注册
     * @param serviceName 服务名
     * @param serviceWrapper 服务包装类
     */
    /**
     * 注册服务
     * @param serviceName
     * @param serviceWrapper
     */
    void register(String serviceName, ServiceWrapper serviceWrapper);
    /**
     * 注销服务
     * @param serviceName
     * @param serviceWrapper
     */
    void unRegister(String serviceName, ServiceWrapper serviceWrapper);
    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    List<ServiceWrapper> getServices(String serviceName);
}

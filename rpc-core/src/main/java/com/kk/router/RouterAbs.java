package com.kk.router;

import com.kk.util.ServiceWrapper;
import com.kk.register.Register;

import java.util.List;

public abstract class RouterAbs implements Router{
    /**
     * 刷新服务列表
     * @param register
     * @param serviceName
     * @return
     */
    @Override
    public List<ServiceWrapper> refresh(Register register, String serviceName) {
        return register.getServices(serviceName);
    }
}
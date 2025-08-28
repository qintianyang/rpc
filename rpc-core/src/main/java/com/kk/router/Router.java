package com.kk.router;


import com.kk.register.Register;
import com.kk.util.ServiceWrapper;
import java.util.List;

public interface Router {
    /**
     * 根据服务名称获取一个服务
     * @param services
     * @return
     */
    ServiceWrapper select(List<ServiceWrapper> services);

    /**
     * 刷新路由
     * @param register
     * @param serviceName
     * @return
     */
    List<ServiceWrapper> refresh(Register register, String serviceName);
}
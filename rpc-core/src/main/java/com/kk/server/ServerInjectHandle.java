package com.kk.server;

import cn.hutool.core.util.ObjectUtil;
import com.kk.annotation.RpcService;
import com.kk.cache.ServerCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerInjectHandle implements BeanPostProcessor , SmartLifecycle {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(RpcService.class)){
            Class<?> api =bean.getClass().getInterfaces()[0];
            if(ObjectUtil.isEmpty(api)){
                throw new RuntimeException("rpc service must implements interface");
            }
            ServerCache.services.put(api.getName(),bean);
        }
        return bean;
    }

    @Override
    public void start() {
        Server server=new Server();
        server.runServer();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
package com.kk.register.redis;


import cn.hutool.core.util.ObjectUtil;
import com.kk.util.ServiceListWrapper;
import com.kk.util.ServiceWrapper;
import com.kk.register.SimpleRegisterAbstract;
import com.kk.register.redis.config.RedisUtil;

import java.util.*;

import static com.kk.constants.RpcConstants.REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION;
import static com.kk.constants.RpcConstants.REDIS_SERVICE_PREFIX_KEY;

/**
 * redis 注册中心
 */
@SuppressWarnings("ConstantConditions")
public class RedisRegister extends SimpleRegisterAbstract {


    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + serviceName;
        // 
        ServiceListWrapper serviceListWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        // 判断是否已经注册服务
        if (ObjectUtil.isNotEmpty(serviceListWrapper.getValue())) {
            List<ServiceWrapper> value = serviceListWrapper.getValue();
            // 判断当前服务在不在
            for(int i = 0;i<value.size();i++){
                ServiceWrapper cur = value.get(i);
                if(cur.equals(serviceWrapper)){
                    return ;
                }
            }
            // 注册服务
            RedisUtil.setWithExpiration(key, serviceListWrapper, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }else{
            ServiceListWrapper serviceListMapWrapper = new ServiceListWrapper();
            ArrayList<ServiceWrapper> serviceList = new ArrayList<>();
            serviceList.add(serviceWrapper);
            serviceListMapWrapper.setValue(serviceList);
            RedisUtil.setWithExpiration(key, serviceListMapWrapper, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + serviceName;
        ServiceListWrapper serviceListWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        if (ObjectUtil.isNotEmpty(serviceListWrapper.getValue())) {
            List<ServiceWrapper> value = serviceListWrapper.getValue();
            Iterator<ServiceWrapper> iterator = value.iterator();
            while (iterator.hasNext()) {
                ServiceWrapper next = iterator.next();
                if (next.equals(serviceWrapper)) {
                    iterator.remove();// 删除当前的元素
                }
            }
            //该服务器没有提供的服务了 清除缓存
            if(serviceListWrapper.getValue().size()==0){
                RedisUtil.delete(key);
            }else{
                RedisUtil.setWithExpiration(key, serviceListWrapper, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION); 
            }
        }
    }
    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        String key = REDIS_SERVICE_PREFIX_KEY + serviceName;
        Map<String, List<ServiceWrapper>> redisService = RedisUtil.get(key, Map.class);
        return redisService.get("value");
    }

}
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
    private RedisUtil redisUtil;
    public RedisRegister(){
    }
    public RedisRegister(String url,String password){
        redisUtil = new RedisUtil(url, password,5);
    }
    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper serviceListMapWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        // 判断服务列表是否创建
        if(ObjectUtil.isNotEmpty(serviceListMapWrapper.getValue())){
            List<ServiceWrapper> value = serviceListMapWrapper.getValue();
            //判断服务列表中是否存在当前服务
            for(int i=0;i<value.size();i++){
                ServiceWrapper cur = value.get(i);
                if(cur.equals(serviceWrapper)){
                    return ;
                }
            }
            value.add(serviceWrapper);
            //注册服务
            RedisUtil.setWithExpiration(key,serviceListMapWrapper,REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
        else{
            ServiceListWrapper serviceListWrapper = new ServiceListWrapper();
            ArrayList<ServiceWrapper>list= new ArrayList<>();
            list.add(serviceWrapper);
            serviceListWrapper.setValue(list);
            RedisUtil.setWithExpiration(key,serviceListWrapper,REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper serviceListMapWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        // 判断服务列表是否创建
        if(ObjectUtil.isNotEmpty(serviceListMapWrapper.getValue())) {
            List<ServiceWrapper> value = serviceListMapWrapper.getValue();
            Iterator<ServiceWrapper> iterator = value.iterator();
            while (iterator.hasNext()) {
                ServiceWrapper next = iterator.next();
                if (next.equals(serviceWrapper)) {
                    iterator.remove(); // 安全删除当前元素
                }
            }
            RedisUtil.setWithExpiration(key,serviceListMapWrapper,REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper listWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        return listWrapper.getValue();
    }
}
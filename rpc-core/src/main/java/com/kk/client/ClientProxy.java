package com.kk.client;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kk.cache.ClientCache;
import com.kk.util.RpcInvocation;
import com.kk.util.ServiceWrapper;
import com.kk.filter.client.ClientFilterChain;
import com.kk.register.Register;
import com.kk.router.Router;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 将 rpcInvocation 发送到服务端 并接收服务端返回结果
 */
@Slf4j
public class ClientProxy implements InvocationHandler {

    private String serviceName;
    public ClientProxy( String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String uuid = UUID.fastUUID().toString();
        RpcInvocation rpcInvocation = new RpcInvocation(uuid,method.getDeclaringClass().getName(),method.getName(), args, method.getParameterTypes(),null,null);
        //过滤器
        ClientFilterChain.doBeforeFilter(rpcInvocation);
        // 获取服务 ，每次都从注册中心获取最新的服务列表
        List<ServiceWrapper> services= ClientCache.register.getServices(serviceName);
        if(ObjectUtil.isEmpty(services)){
            log.error("service not found");
            throw new Exception("service not found");
        }
        // 负载均衡
        ServiceWrapper select= ClientCache.router.select(services);
        log.info("select service:{} - {}", select.getDomain(), select.getPort());
        Object result = null;
        //容错机制
        int count = 0;
        int retryTimes = ClientCache.clientConfig.getRetryTimes();
        long intervalTimes = ClientCache.clientConfig.getRetryInterval();
        while (count++ < retryTimes) {
            try {
                CompletableFuture<Object> completableFuture = Client.sendMessage(select, rpcInvocation);
                result = completableFuture.get();
                // 服务器异常会存储在RpcInvocation的exception字段中
                if(ObjectUtil.isNotEmpty(((RpcInvocation) result).getE())){
                    throw new Exception(((RpcInvocation) result).getE());
                }
                log.info("客户端接受:{}",result);
                //服务后过滤器
                ClientFilterChain.doAfterFilter((RpcInvocation) result);
                break;

            } catch (Throwable e) {
                // 异常处理的情况 线程等待
                ThreadUtil.sleep(intervalTimes * count);
                log.warn("send message error:{} -{}",e.getMessage(),e.getClass());
                // 如果是最后一次重试，则抛出异常，终止重试
                if (count >= retryTimes) {
                    throw new RuntimeException("server send mesage error,count used");
                }
            }
        }
        return ((RpcInvocation)result).getResult();

    }
}
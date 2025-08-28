package com.kk.client;

import cn.hutool.core.lang.UUID;
import com.kk.util.RpcInvocation;
import com.kk.filter.client.ClientFilterChain;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String uuid = UUID.fastUUID().toString();
        RpcInvocation rpcInvocation = new RpcInvocation(uuid,method.getDeclaringClass().getName(),method.getName(), args,null);

        //过滤器
        ClientFilterChain.doFilter(rpcInvocation);
        Object result = null;
        int count =3;
        while (--count>0) {
            try {
                CompletableFuture<Object> completableFuture = Client.sendMessage(rpcInvocation);
                result = completableFuture.get();
            } catch (Throwable e) {
                log.warn("客户端发送消息失败{} -{}",e.getMessage(),e.getClass());
            }
        }
        return ((RpcInvocation)result).getResult();

    }
}
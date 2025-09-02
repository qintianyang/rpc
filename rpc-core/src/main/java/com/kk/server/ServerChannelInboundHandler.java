package com.kk.server;

import cn.hutool.core.util.ObjectUtil;
import com.kk.util.RpcInvocation;
import com.kk.util.RpcProtocol;
import com.kk.serializer.jackson.JacksonSerializer;

import com.kk.exception.server.NotFoundServiceException;
import com.kk.filter.server.ServerFilterChain;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

import static com.kk.cache.ServerCache.serializer;
import static com.kk.cache.ServerCache.services;

@Slf4j
public class ServerChannelInboundHandler extends SimpleChannelInboundHandler<RpcInvocation> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcInvocation rpcInvocation) throws Exception {
        RpcProtocol rpcProtocol = null;
        try {
            rpcProtocol = new RpcProtocol();
            ServerFilterChain.doBeforeFilter(rpcInvocation);
            Object clasBean = services.get(rpcInvocation.getClassName());
            System.out.println(clasBean);
            if(ObjectUtil.isEmpty(clasBean)){
                rpcInvocation.setE(new NotFoundServiceException("找不到对应的服务"));
            }
            else{
                // 反射调用方法
                Method method = clasBean.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParamTypes());
                Object ret = method.invoke(clasBean, rpcInvocation.getParams());
                rpcInvocation.setResult(ret);
                // 后置处理
                ServerFilterChain.doAfterFilter(rpcInvocation);
                // 测试异常
                // System.out.println(1/0);
            }
        }catch (Throwable e){
            rpcInvocation.setResult(null);
            rpcInvocation.setE(e);
        } finally {
            ServerFilterChain.doAfterFilter(rpcInvocation);
            byte[] data = serializer.serialize(rpcInvocation);
            rpcProtocol.setContent(data);
            rpcProtocol.setContentLength(data.length);
            ctx.writeAndFlush(rpcProtocol);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常信息：\r\n" + cause.getMessage());
        Channel channel = ctx.channel();
        channel.close().addListener(future -> {
            if (future.isSuccess()) {
                log.info("管道关闭成功");
            } else {
                log.error("管道关闭失败", future.cause());
            }
        });
    }
}
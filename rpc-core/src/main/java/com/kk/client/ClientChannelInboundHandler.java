package com.kk.client;


import com.kk.util.RpcInvocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static com.kk.cache.CachePool.resultCache;


@Slf4j
public class ClientChannelInboundHandler extends ChannelInboundHandlerAdapter {
        /**
     * 当通道有读取事件时，会触发此方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            RpcInvocation data = (RpcInvocation) msg;
            ((CompletableFuture)resultCache.remove(data.getUuid())).complete(msg);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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

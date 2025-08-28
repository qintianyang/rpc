package com.kk.server;

import com.kk.util.RpcInvocation;
import com.kk.util.RpcProtocol;
import com.kk.jackson.JacksonSerialize;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import io.netty.util.ReferenceCountUtil;

@Slf4j
public class ServerChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.debug("获取到数据: {}", msg);
            RpcInvocation rpcInvocation = (RpcInvocation) msg;
            rpcInvocation.setResult("ok");
            byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
            RpcProtocol rpcProtocol = new RpcProtocol();
            rpcProtocol.setContentLength(serialize.length);
            rpcProtocol.setContent(serialize);
            ctx.writeAndFlush(rpcProtocol);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
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
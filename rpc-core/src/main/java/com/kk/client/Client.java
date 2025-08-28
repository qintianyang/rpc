package com.kk.client;


import com.kk.util.MessageDecoder;
import com.kk.util.MessageEncoder;
import com.kk.util.RpcInvocation;
import com.kk.util.RpcProtocol;
import com.kk.jackson.JacksonSerialize;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;
// import com.kk.cache.CachePool;
import static com.kk.cache.CachePool.resultCache;

import java.net.InetSocketAddress;


@Slf4j
public class Client {
    private static Bootstrap bootstrap = new Bootstrap();
    static {
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new ClientChannelInboundHandler());
            }
        });
    }
    
    public static ChannelFuture getChannelFuture() throws InterruptedException{
        return bootstrap.connect(new InetSocketAddress("127.0.0.1", 6636));
    }

    public static CompletableFuture<Object> sendMessage(RpcInvocation rpcInvocation){
        Channel channel = null;
        CompletableFuture<Object> future = new CompletableFuture<>();
        RpcProtocol rpcProtocol = new RpcProtocol();
        try {
            byte[] body= new JacksonSerialize().serialize(rpcInvocation);
            rpcProtocol.setContentLength(body.length);
            rpcProtocol.setContent(body);
            channel = getChannelFuture().channel();
            resultCache.put(rpcInvocation.getUuid(),future);//将请求根据id进行缓存用于后续接收服务端返回结果

            if(!channel.isActive()){
                bootstrap.group().shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcProtocol).addListener((ChannelFutureListener)future1 -> {
                if(future1.isSuccess()){
                    log.info("客户端发送消息成功:{}", rpcInvocation);
                }else{
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                    log.error("客户端发送消息失败",future1.cause());
                }
            });
        }catch (Exception e){
            resultCache.remove(rpcInvocation.getUuid());
            log.error("客户端发送消息异常",e);
            Thread.currentThread().interrupt();
        }
        return future;
    }
}

    // private static ChannelFuture connect(InetSocketAddress address) throws InterruptedException {
    //     bootstrap.group(new NioEventLoopGroup());
    //     bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    //     bootstrap.channel(NioSocketChannel.class);
    //     bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
    //         @Override
    //         protected void initChannel(NioSocketChannel channel) throws Exception {
    //             channel.pipeline().addLast(new MessageEncoder());
    //             channel.pipeline().addLast(new MessageDecoder());
    //             channel.pipeline().addLast(new ClientChannelInboundHandler());
    //         }
    //     });
    //     ChannelFuture channelFuture = bootstrap.connect(address).sync();
    //     if (channelFuture.isSuccess()) {
    //         log.debug("服务器连接成功: {} - {}", address.getHostName(), address.getPort());
    //     } else {
    //         log.debug("服务器连接失败: {} - {}", address.getHostName(), address.getPort());
    //     }
    //     return channelFuture;
    // }

    // public static void sendMessage(RpcProtocol rpcProtocol) throws InterruptedException {
    //     ChannelFuture channelFuture = getChannelFuture();
    //     channelFuture.channel().writeAndFlush(rpcProtocol);
    // }

    // public static void main(String[] args) {
    //     Client client = new Client();
    //     client.test();
    // }

    // public void test() {
    //     RpcProtocol rpcProtocol = new RpcProtocol();
    //     RpcInvocation rpcInvocation = new RpcInvocation();
    //     rpcInvocation.setClassName("com.kk.server.HelloService");
    //     rpcInvocation.setMethodName("hello");
    //     try {
    //         byte[] body = new JacksonSerialize().serialize(rpcInvocation);
    //         rpcProtocol.setContentLength(body.length);
    //         rpcProtocol.setContent(body);
    //         sendMessage(rpcProtocol);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // public static void main(String[] args) throws Exception {
        // 创建 Netty 客户端的启动类实例
    //     Bootstrap bootstrap = new Bootstrap();
    //     // 配置事件循环组（EventLoopGroup），用于处理所有事件（连接、读写等）
    //     bootstrap.group(new NioEventLoopGroup());
    //     // 指定通道类型为 NIO Socket 通道
    //     bootstrap.channel(NioSocketChannel.class);
    //     // 每个新连接初始化管道（Pipeline）
    //     bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
    //         @Override
    //         protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
    //             // 添加解码器 二进制数据 反序列为消息对象
    //             nioSocketChannel.pipeline().addLast(new MessageDecoder());
    //             // 添加编码器 消息对象 序列为二进制数据
    //             nioSocketChannel.pipeline().addLast(new MessageEncoder());
    //             nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
    //                 @Override
    //                 public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
    //                     log.info("客户端收到的消息是{}",msg);
    //                     super.channelRead(ctx, msg);
    //                 }
    //             });
    //         }
    //     });

    //     // 连接到服务器
    //     Channel channel = bootstrap.connect("127.0.0.1", 6636).sync().channel();
    //     //
    //     log.info("消息准备发送给");

    //     // 创建了一个RPC的目标对象，封装目标的类名和方法
    //     RpcInvocation rpcInvocation = new RpcInvocation();
    //     rpcInvocation.setClassName("com.kk.service.impl.HelloServiceImpl"); // 设置目标类名
    //     rpcInvocation.setMethodName("sayHello"); // 设置目标方法名
        
    //     // 创建一个协议对象，用于封装序列化后的数据
    //     RpcProtocol rpcProtocol = new RpcProtocol();

    //     // 序列化转化为字节数组
    //     byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
        
    //     // 协议对象设置内容长度和内容
    //     rpcProtocol.setContentLength(serialize.length);
    //     rpcProtocol.setContent(serialize);

    //     // 发送协议对象
    //     channel.writeAndFlush(rpcProtocol);
    // }


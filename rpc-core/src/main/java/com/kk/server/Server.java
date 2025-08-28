package com.kk.server;

import lombok.extern.slf4j.Slf4j;

// import com.kk.register.redis.RedisRegister;
import com.kk.util.MessageDecoder;
import com.kk.util.MessageEncoder;
import com.kk.util.ServiceWrapper;
import com.kk.register.redis.RedisRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.kk.register.Register;

@Slf4j
public class Server {
    private static final ServerBootstrap serverBootstrap=new ServerBootstrap();
    // RedisRegister redisRegister = new RedisRegister();
    Register redisRegister = new RedisRegister();

    public ChannelFuture runServer() {
        log.info("启动服务器中..");
        serverBootstrap.group(new NioEventLoopGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new ServerChannelInboundHandler());
            }
        });
        return serverBootstrap.bind(6636).addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                // redisRegister.register("test", "127.0.0.1", 6636);
                ServiceWrapper serviceWrapper = new ServiceWrapper();
                serviceWrapper.setDomain("127.0.0.1");
                serviceWrapper.setPort(6636);
                redisRegister.register("test",serviceWrapper);
                Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
                log.info("服务器启动成功");
            } else {
                log.error("服务器启动失败");
            }
        });
    }

    public void stopServer() {
        // serverBootstrap.config().group().shutdownGracefully();
        ServiceWrapper serviceWrapper = new ServiceWrapper();
        serviceWrapper.setDomain("127.0.0.1");
        serviceWrapper.setPort(6636);
        redisRegister.unRegister("test",serviceWrapper);
        serverBootstrap.config().group().shutdownGracefully();
        log.info("服务器关闭成功！");
    }

    // public void stopPre(ChannelFuture serverChannelFuture) {
    //     serverChannelFuture.channel().closeFuture().addListener(future -> {
    //         if (future.isSuccess()) {
    //             redisRegister.unRegister("test", "127.0.0.1", 6636);
    //             log.info("服务器关闭成功");
    //         }
    //     });
    // }

    public static void main(String[] args) {
        // runServer();
        Server server = new Server();
        // server.stopPre(server.runServer());
        server.runServer();
    }


    // public static void main(String[] args) {
    //     ServerBootstrap serverBootstrap = new ServerBootstrap();
    //     serverBootstrap.group(new NioEventLoopGroup());
    //     serverBootstrap.channel(NioServerSocketChannel.class);
    //     serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
    //         @Override
    //         protected void initChannel(NioSocketChannel ch) throws Exception {
    //             ch.pipeline().addLast(new MessageEncoder());
    //             ch.pipeline().addLast(new MessageDecoder());
    //             ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
    //                 @Override
    //                 public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
    //                     log.debug("服务端收到的消息是{}",msg);
    //                     RpcInvocation rpcInvocation = (RpcInvocation) msg;
    //                     rpcInvocation.setResult("ok");
    //                     byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
    //                     RpcProtocol protocol = new RpcProtocol();
    //                     protocol.setContentLength(serialize.length);
    //                     protocol.setContent(serialize);
    //                     ctx.channel().writeAndFlush(protocol);
    //                 }
    //             });
    //         }
    //     });
    //     serverBootstrap.bind(6636);
    // }


}

package com.kk.client;

import cn.hutool.core.util.ObjectUtil;
import com.kk.config.ClientConfig;
import com.kk.config.ConfigLoader;
import com.kk.cache.ClientCache;
import com.kk.filter.client.ClientFilter;
import com.kk.filter.client.ClientFilterChain;
import com.kk.util.*;
import com.kk.serializer.jackson.JacksonSerializer;
import com.kk.register.Register;
import com.kk.router.Router;
import com.kk.serializer.Serializer;
import com.kk.spi.ExtraLoader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

import static com.kk.cache.CachePool.extraLoader;
import static com.kk.cache.CachePool.RESULT_CACHE;

/**
 * 用户端
 */
@Slf4j
public class Client {

    // 创建Netty客户端引导类，用于配置和启动客户端
    private static Bootstrap bootstrap = new Bootstrap();
    static {
        initConfiguration();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioSocketChannel.class);
        // 设置接收缓冲区自适应,最小2048,最大1024*10,初始1024*10,以防止服务端返回数据过大导致内存溢出
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,
                new AdaptiveRecvByteBufAllocator(2048, 1024 * 10, 1024 * 10));
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder(ClientCache.serializer));
                channel.pipeline().addLast(new ClientChannelInboundHandler());
            }
        });
    }

    /**
     * 初始化配置
     */
    public static void initConfiguration(){
        try {
            ClientConfig clientConfig = ConfigLoader.loadClientProperties();
            extraLoader.loadExtension(Register.class);
            extraLoader.loadExtension(Serializer.class);
            extraLoader.loadExtension(Router.class);
            LinkedHashMap<String, Class> registerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Register.class.getName());
            LinkedHashMap<String, Class> serializerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Serializer.class.getName());
            LinkedHashMap<String, Class> routerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Router.class.getName());
            //过滤器
            LinkedHashMap<String,Class> clientFilterClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(ClientFilter.class.getName());
            if (ObjectUtil.isNotEmpty(clientFilterClass)) {
                log.info("加载客户端过滤器");
                Set<String> clientFilterNames = clientFilterClass.keySet();
                for (String key : clientFilterNames) {
                    if(!key.toLowerCase().contains("after")){
                        ClientFilterChain.addBeforeFilter((ClientFilter) clientFilterClass.get(key).newInstance());
                    }
                    else{
                        ClientFilterChain.addAfterFilter((ClientFilter) clientFilterClass.get(key).newInstance());
                    }
                }
            }
            // 注册中心
            Class regCls = registerClass.get(clientConfig.getRegisterType());
            if(ObjectUtil.isEmpty(regCls)){
                throw new RuntimeException("register type not found");
            }
            System.out.println("---------------------------------");
            System.out.println(clientConfig);
            ClientCache.register = (Register) regCls.getConstructor(String.class, String.class)
                    .newInstance(clientConfig.getRegisterAddress(),
                            clientConfig.getRegisterPassword());
            // 序列化
            Class serCls = serializerClass.get(clientConfig.getClientSerialize());
            if(ObjectUtil.isEmpty(serCls)){
                throw new RuntimeException("serializer not found");
            }
            // 序列化
            ClientCache.serializer = (Serializer) serCls.newInstance();
            Class routerCls = routerClass.get(clientConfig.getRouterType());
            if(ObjectUtil.isEmpty(routerCls)){
                throw new RuntimeException("router not found");
            }
            ClientCache.router = (Router) routerCls.newInstance();
            ClientCache.clientConfig = clientConfig;
        }catch (Exception e){
            log.error("client init error",e);
        }
    }

    /**
     * 建立连接获取通道
     * @param router
     * @return
     * @throws InterruptedException
     */
    public static ChannelFuture getChannelFuture(ServiceWrapper router) throws InterruptedException {
        return bootstrap.connect(new InetSocketAddress(router.getDomain(), router.getPort())).sync();
    }

    /**
     * 发送消息
     * @param router
     * @param rpcInvocation
     * @return
     */
    public static CompletableFuture<Object> sendMessage(ServiceWrapper router, RpcInvocation rpcInvocation){
        Channel channel = null;
        CompletableFuture<Object> future = new CompletableFuture<>();
        RpcProtocol rpcProtocol = new RpcProtocol();
        try {
            byte[] body= new JacksonSerializer().serialize(rpcInvocation);
            rpcProtocol.setContentLength(body.length);
            rpcProtocol.setContent(body);
            channel = getChannelFuture(router).channel();
            RESULT_CACHE.put(rpcInvocation.getUuid(),future);//将请求根据id进行缓存用于后续接收服务端返回结果

            if(!channel.isActive()){
                bootstrap.group().shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcProtocol).addListener((ChannelFutureListener)future1 -> {
                if(future1.isSuccess()){
                    log.info("send message success:{}", rpcInvocation);
                }else{
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                    log.error("send message error:{}",future1.cause());
                }
            });
        }catch (Exception e){
            RESULT_CACHE.remove(rpcInvocation.getUuid());
            log.error("send message error:{}",e);
            Thread.currentThread().interrupt();
        }
        return future;
    }
}
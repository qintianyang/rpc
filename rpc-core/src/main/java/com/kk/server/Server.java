package com.kk.server;

import cn.hutool.core.util.ObjectUtil;
import com.kk.cache.ServerCache;
import com.kk.util.MessageDecoder;
import com.kk.util.MessageEncoder;
import com.kk.util.ServiceWrapper;
import com.kk.config.ConfigLoader;
import com.kk.filter.server.ServerFilter;
import com.kk.filter.server.ServerFilterChain;
import com.kk.config.ServerConfig;
import com.kk.register.Register;
import com.kk.serializer.Serializer;
import com.kk.spi.ExtraLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Set;

import static com.kk.cache.CachePool.extraLoader;
import static com.kk.cache.ServerCache.register;
import static com.kk.cache.ServerCache.serverConfig;

@Slf4j
public class Server {
    private static final ServerBootstrap serverBootstrap=new ServerBootstrap();
    private NioEventLoopGroup bootGroup = new NioEventLoopGroup();
    private NioEventLoopGroup workGroup = new NioEventLoopGroup();
    public void initConfiguration(){
        try{
            ServerConfig serverConfig = ConfigLoader.loadServerProperties();
            // 加载扩展 序列化 注册中心 过滤
            extraLoader.loadExtension(Serializer.class);
            extraLoader.loadExtension(Register.class);
            extraLoader.loadExtension(ServerFilter.class);
            // 加载扩展 注册中心 序列化
            LinkedHashMap<String, Class> registerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Register.class.getName());
            LinkedHashMap<String, Class> serializeClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Serializer.class.getName());
            LinkedHashMap<String, Class> serverFilterClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(ServerFilter.class.getName());
            
            // 过滤器
            if(ObjectUtil.isNotEmpty(serverFilterClass)){
                Set<String> serverFilterKeys = serverFilterClass.keySet();
                for(String key:serverFilterKeys){
                    if(key.toLowerCase().contains("after")){
                        ServerFilterChain.addAfterFilter((ServerFilter) serverFilterClass.get(key).newInstance());
                    }else{
                        ServerFilterChain.addBeforeFilter((ServerFilter) serverFilterClass.get(key).newInstance());
                    }
                }
            }

            // 注册中心
            Class regCls = registerClass.get(serverConfig.getRegisterType());
            if (ObjectUtil.isEmpty(regCls)) {
                throw new RuntimeException("register type not found");
            }
            register = (Register) regCls.newInstance();
            // 序列化
            Class serCls = serializeClass.get(serverConfig.getServerSerialize());
            if (ObjectUtil.isEmpty(serCls)) {
                throw new RuntimeException("serialize type not found");
            }
            ServerCache.serializer = (Serializer) serCls.newInstance();
            ServerCache.serverConfig = serverConfig;
        }catch (Exception e){
            log.error("init server fail",e);
        }
    }
    public ChannelFuture runServer() {
        log.info("server start");
        initConfiguration();
        serverBootstrap.group(bootGroup,workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageDecoder(ServerCache.serializer));
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new ServerChannelInboundHandler());
            }
        });
        return serverBootstrap.bind(serverConfig.getServerPort()).addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                ServiceWrapper serviceWrapper = new ServiceWrapper();
                serviceWrapper.setPort(serverConfig.getServerPort());
                serviceWrapper.setDomain("127.0.0.1");
                register.register(serverConfig.getApplicationName(), serviceWrapper);
                Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
                log.info("server start success");
            } else {
                log.error("server start fail:{}",channelFuture.cause());
            }
        });
    }
    public void stopServer() {
        ServiceWrapper serviceWrapper = new ServiceWrapper();
        serviceWrapper.setDomain("127.0.0.1");
        serviceWrapper.setPort(serverConfig.getServerPort());
        // System.out.println(serverConfig);
        register.unRegister(serverConfig.getApplicationName(), serviceWrapper);
        // serverBootstrap.group().shutdownGracefully();
        bootGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        log.info("server stop success");
    }

}
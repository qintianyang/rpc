package com.kk.cache;
import com.kk.util.RpcProtocol;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CachePool {
    /**
     * 发送消息缓存队列
     */
    public static final ArrayBlockingQueue<RpcProtocol> sendDataCacheQueue = new ArrayBlockingQueue<>(100);
    /*
     * 缓存服务调用的结果
     *  缓存服务端调用的结果 map[请求UUID]=返回结果
     *  管道返回结果会根据请求UUID匹配返回结果
     */
    public static final ConcurrentHashMap<String, Object> resultCache = new ConcurrentHashMap<>();
}

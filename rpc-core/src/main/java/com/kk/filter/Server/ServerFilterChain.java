package com.kk.filter.Server;

import com.kk.util.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

public class ServerFilterChain {
    public static List<ServerFilter> filters=new ArrayList<>();
    public static void addServerFilter(ServerFilter serverFilter)
    {
        filters.add(serverFilter);
    }
    public static void doFilter(RpcInvocation rpcInvocation)
    {
        for (ServerFilter filter : filters) {
            filter.doFilter(rpcInvocation);
        }
    }
}
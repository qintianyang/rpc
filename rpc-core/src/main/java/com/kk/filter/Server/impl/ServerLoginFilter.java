package com.kk.filter.Server.impl;

import com.kk.filter.Server.ServerFilter;
import com.kk.util.RpcInvocation;

public class ServerLoginFilter implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        System.out.println("服务端登录过滤");
    }
}

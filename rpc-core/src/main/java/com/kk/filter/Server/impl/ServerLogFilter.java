package com.kk.filter.server.impl;

import com.kk.filter.server.ServerFilter;
import com.kk.util.RpcInvocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerLogFilter implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info("server log filter:{}", rpcInvocation);
    }
}

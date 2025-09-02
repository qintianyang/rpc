package com.kk.filter.client.impl;

import lombok.extern.slf4j.Slf4j;
import com.kk.filter.client.ClientFilter;
import com.kk.util.RpcInvocation;

@Slf4j
public class ClientLogAfterFilterImpl implements ClientFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info("client log after filter,invocation:{}",rpcInvocation);
    }
}

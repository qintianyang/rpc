package com.kk.filter.client.impl;

import com.kk.util.RpcInvocation;
import com.kk.filter.client.ClientFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientLogRecordFilterImpl implements ClientFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info("client log record filter,invocation:{}",rpcInvocation);
    }
}
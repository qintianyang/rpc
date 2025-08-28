package com.kk.filter.client;

import com.kk.filter.Filter;
import com.kk.util.RpcInvocation;

public interface ClientFilter extends Filter {
    void doFilter(RpcInvocation rpcInvocation);
}
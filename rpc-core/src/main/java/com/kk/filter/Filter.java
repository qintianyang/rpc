package com.kk.filter;

import com.kk.util.RpcInvocation;

public interface Filter {

    void doFilter(RpcInvocation rpcInvocation);
}

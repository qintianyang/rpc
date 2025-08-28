package com.kk.filter.Server;

import com.kk.filter.Filter;
import com.kk.util.RpcInvocation;

public interface ServerFilter extends Filter {
        void doFilter(RpcInvocation rpcInvocation);
}
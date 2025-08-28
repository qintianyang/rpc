package com.kk.filter.client;

import com.kk.util.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

public class ClientFilterChain {
    private  static List<ClientFilter> clientFilters=new ArrayList<>();

    public static void addClientFilter(ClientFilter clientFilter)
    {
        clientFilters.add(clientFilter);
    }

    public static void doFilter(RpcInvocation rpcInvocation)
    {
        for (ClientFilter clientFilter : clientFilters) {
            clientFilter.doFilter(rpcInvocation);
        }
    }
}
package com.kk.filter.client;

import com.kk.util.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

public class ClientFilterChain {
    
    private  static List<ClientFilter> clientAfterFilters=new ArrayList<>();
    private  static List<ClientFilter> clientBeforeFilters=new ArrayList<>();

    public static void addBeforeFilter(ClientFilter clientFilter){
        clientBeforeFilters.add(clientFilter);
    }
    public static void addAfterFilter(ClientFilter clientFilter){
        clientAfterFilters.add(clientFilter);
    }
    public static void doBeforeFilter(RpcInvocation invocation){
        for (ClientFilter clientFilter : clientBeforeFilters) {
            clientFilter.doFilter(invocation);
        }
    }
    public static void doAfterFilter(RpcInvocation invocation){
        for (ClientFilter clientFilter : clientAfterFilters) {
            clientFilter.doFilter(invocation);
        }
    }
}
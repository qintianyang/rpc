package com.kk.filter.server;

import com.kk.util.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

public class ServerFilterChain {

    private static List<ServerFilter> serverBeforeFilters = new ArrayList<>();
    private static List<ServerFilter> serverAfterFilters = new ArrayList<>();

    public static void addBeforeFilter(ServerFilter serverFilter) {
        serverBeforeFilters.add(serverFilter);
    }
    public static void addAfterFilter(ServerFilter serverFilter) {
        serverAfterFilters.add(serverFilter);
    }
    public static void doBeforeFilter(RpcInvocation invocation) {
        for (ServerFilter serverFilter : serverBeforeFilters) {
            serverFilter.doFilter(invocation);
        }
    }
    public static void doAfterFilter(RpcInvocation invocation) {
        for (ServerFilter serverFilter : serverAfterFilters) {
            serverFilter.doFilter(invocation);
        }
    }
}
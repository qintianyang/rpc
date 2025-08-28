package com.kk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kk.server.Server;


class Test {
    public static void main(String[] args)throws JsonProcessingException {
        Server server = new Server();
        server.stopServer();
//        InetAddress localHost = null;
//        try {
//            localHost = InetAddress.getLocalHost();
//            String ipAddress = localHost.getHostAddress();
//            String hostName = localHost.getHostName();
//            System.out.println(ipAddress);
//            System.out.println(hostName);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
    }
}
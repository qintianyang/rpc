package com.kk;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import com.kk.annotation.EnableRpcServer;

@SpringBootApplication
@EnableRpcServer
public class ShopAppServer {
    public static void main(String[] args) {
        SpringApplication.run(ShopAppServer.class, args);
    }
}

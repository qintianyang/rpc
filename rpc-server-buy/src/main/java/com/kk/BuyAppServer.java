package com.kk;

import com.kk.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableRpcServer
@SpringBootApplication
public class BuyAppServer {
    public static void main(String[] args) {
        SpringApplication.run(BuyAppServer.class, args);
    }
}

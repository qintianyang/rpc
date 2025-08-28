package com.kk;

import com.kk.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpcServer
public class ServerStart {
    public static void main(String[] args) {
       SpringApplication.run(ServerStart.class, args);
    }
}
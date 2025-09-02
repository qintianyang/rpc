package com.kk.client.controller;

import api.MessageService;
import com.kk.annotation.RpcReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientController {
    
    @RpcReference(serviceName = "buy")
    private MessageService messageService1;

    @RpcReference(serviceName = "shop")
    private MessageService messageService2;

    @RequestMapping("/test1")
    public String test1() {
        return messageService1.getMessage("hello");
    }

    @RequestMapping("/test2")
    public String test2() {
        return messageService2.getMessage("hello");
    }
}

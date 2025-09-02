package com.kk.api;

import api.MessageService;
import com.kk.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public String getMessage(String message) {
        return "Buy Service";
    }
}

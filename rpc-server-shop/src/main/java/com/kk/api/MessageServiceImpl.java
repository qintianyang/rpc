package com.kk.api;

import org.springframework.stereotype.Service;
import api.MessageService;
import com.kk.annotation.RpcService;

@Service
@RpcService
public class MessageServiceImpl implements MessageService {
    @Override
    public String getMessage(String message) {
        return "shop service!";
    }
}

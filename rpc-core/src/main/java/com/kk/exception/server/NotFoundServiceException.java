package com.kk.exception.server;

import org.springframework.beans.BeansException;

public class NotFoundServiceException extends BeansException {
    public NotFoundServiceException(String msg) {
        super(msg);
    }
}
package com.kk.exception.client;

import com.kk.exception.BaseException;

public class RequestTimeoutException extends BaseException {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
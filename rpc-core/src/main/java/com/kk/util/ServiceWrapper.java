package com.kk.util;

import java.io.Serializable;

import lombok.Data;

@Data
public class ServiceWrapper implements Serializable{
    private String domain;
    private int port;

    public String getUrl() {
        return  domain + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServiceWrapper && getUrl().equals(((ServiceWrapper) obj).getUrl());
    }
}

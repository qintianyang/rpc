package com.kk.util;

import lombok.Data;

@Data
public class ServiceWrapper {
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

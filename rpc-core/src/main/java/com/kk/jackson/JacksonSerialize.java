package com.kk.jackson;

import com.kk.serialize.Serialize;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerialize implements Serialize{

    // private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return new ObjectMapper().readValue(bytes, clazz);
    }
}

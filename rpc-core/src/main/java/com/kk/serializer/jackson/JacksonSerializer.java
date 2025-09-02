package com.kk.serializer.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kk.serializer.Serializer;

public class JacksonSerializer implements Serializer {
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception{
        return new ObjectMapper().readValue(bytes, clazz);
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsBytes(obj);
    }
}
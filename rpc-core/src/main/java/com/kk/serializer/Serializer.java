package com.kk.serializer;

public interface Serializer {
    // 序列化方法：将任意 Java 对象转换为字节数组
    byte[] serialize(Object obj) throws Exception;
    // 反序列化方法：将字节数组恢复为指定类型的 Java 对象
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}

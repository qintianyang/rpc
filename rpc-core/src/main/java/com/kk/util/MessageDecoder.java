package com.kk.util;

import com.kk.jackson.JacksonSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


import static com.kk.constants.RpcConstants.MAGIC_NUMBER;


public class MessageDecoder extends ByteToMessageDecoder {
    private static final int BASE_LENGTH = Short.BYTES + Integer.BYTES;

    @Override
    protected void decode(ChannelHandlerContext ctx, 
                            ByteBuf byteBuf,
                             List<Object> list) throws Exception {
        // 在这里实现您的解码逻辑
        // 例如，读取字节并反序列化为 RpcProtocol 对象，然后添加到 list 中

        // 1. 检查可读字节数是否足够解析基本长度（魔数 + 数据长度）
        if (byteBuf.readableBytes() > BASE_LENGTH) {
            // 标记当前读指针位置，以便在数据不足时回滚
            // 2. 读取魔数并进行校验
            // MAGIC_NUMBER 应该是一个常量，例如在 RpcConstants 中定义
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                // throw new IllegalArgumentException("Invalid magic number");
                ctx.close();
                return;
            }
            // 3. 读取数据内容的长度
            int length = byteBuf.readInt();
            // 如果数据不完整，将读指针回滚到标记位置，等待更多数据到来
            if (byteBuf.readableBytes() < length) {
                ctx.close();
                return;
            }
            // 5. 使用 JacksonSerializer 将字节数组反序列化为 RpcInvocation 对象
            // RpcInvocation 应该是您的 RPC 调用请求或响应的封装对象
            // 将反序列化后的对象添加到 list 中，Netty 会将 list 中的对象传递给下一个 ChannelInboundHandler
            byte[] content = new byte[length];
            byteBuf.readBytes(content);
            list.add(new JacksonSerialize().deserialize(content, RpcInvocation.class));
        }
    }
}

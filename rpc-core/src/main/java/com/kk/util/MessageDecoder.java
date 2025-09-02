package com.kk.util;

import com.kk.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.kk.constants.RpcConstants.MAGIC_NUMBER;

/**
 *解码
 */
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int BASE_LENGTH = Short.BYTES + Integer.BYTES;
    private Serializer serializer;
    public MessageDecoder(Serializer serializer) {
        this.serializer = serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > BASE_LENGTH) {
            // 魔数判定
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                ctx.close();
                return;
            }
            // 内容长度判定
            int length = byteBuf.readInt();
            if (length > byteBuf.readableBytes()) {
                ctx.close();
                return;
            }
            // 读取内容
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            list.add(serializer.deserialize(body, RpcInvocation.class));
        }
    }
}
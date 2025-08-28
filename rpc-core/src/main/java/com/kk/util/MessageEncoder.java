package com.kk.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<RpcProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol message, ByteBuf byteBuf) throws Exception {
        // 在这里实现您的编码逻辑
        // 例如，将 rpcProtocol 对象序列化为字节，并写入到 byteBuf 中
        //
        byteBuf.writeShort(message.getMagicNumber());
        //
        byteBuf.writeInt(message.getContentLength());
        //
        byteBuf.writeBytes(message.getContent());
    }
}

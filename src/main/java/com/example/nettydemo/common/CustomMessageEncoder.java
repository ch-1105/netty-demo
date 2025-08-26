package com.example.nettydemo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

/**
 * 将CustomMessage对象编码为字节流，包含长度字段，以便通过网络发送。
 */
// 核心组件：MessageToByteEncoder
// <CustomMessage>：要编码的出站消息类型
public class CustomMessageEncoder extends MessageToByteEncoder<CustomMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMessage msg, ByteBuf out) throws Exception {
        byte[] contentBytes = msg.getContent().getBytes(CharsetUtil.UTF_8);
        out.writeInt(contentBytes.length); // 写入4字节的消息长度
        out.writeBytes(contentBytes);      // 写入消息内容
        System.out.println("Encoder encoded message to ByteBuf (length " + contentBytes.length + "): " + msg.getContent());
    }
}
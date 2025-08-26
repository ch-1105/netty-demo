package com.example.nettydemo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * 将LengthFieldBasedFrameDecoder解析出的消息内容(ByteBuf)进一步解码为CustomMessage对象。
 */
// 核心组件：MessageToMessageDecoder
// MessageToMessageDecoder是ByteToMessageDecoder的子类，用于将一种消息类型转换为另一种消息类型。
// <ByteBuf>：从前一个Handler接收的入站消息类型
// <CustomMessage>：转换后输出的入站消息类型
public class CustomMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // LengthFieldBasedFrameDecoder已经处理了长度字段和粘包/拆包。
        // 此时msg中只包含纯粹的消息内容（字符串的字节表示）。
        // 核心组件：ByteBuf
        String content = msg.toString(CharsetUtil.UTF_8); // 将ByteBuf内容转换为字符串
        out.add(new CustomMessage(content)); // 将CustomMessage对象添加到输出列表
    }
}
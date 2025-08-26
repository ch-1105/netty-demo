package com.example.nettydemo.client;

import com.example.nettydemo.common.CustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty TCP客户端的业务逻辑处理器。
 * 用于在连接建立后发送多条消息，通过延迟刷新模拟粘包。
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<CustomMessage> {
    private int counter = 0;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端已激活，准备发送数据...");
        String[] messages = {
                "Hello Netty!",
                "This is a Test Message.",
                "Short.",
                "Long message to demonstrate how LengthFieldBasedFrameDecoder works with variable length data.",
                "Another small one."
        };
        // (1) 核心改变：逐个写入CustomMessage对象，但不立即刷新
        // 核心组件：ChannelHandlerContext
        // 这样，多个CustomMessage对象会经过CustomMessageEncoder编码成ByteBuf，
        // 但这些ByteBuf会累积在出站缓冲区中，直到执行一次flush操作。
        // 这模拟了TCP的粘包，因为它们可能被底层Socket一次性发送。
        for (String msgContent : messages) {
            CustomMessage customMessage = new CustomMessage(msgContent);
            // 调用ctx.write()，消息会沿着pipeline向下传递，遇到CustomMessageEncoder进行编码
            // 编码后的ByteBuf会暂存在ChannelOutboundBuffer中，等待flush
            ctx.write(customMessage);
            System.out.println("客户端写入消息：" + customMessage.getContent());
        }
        // (2) 统一刷新ChannelOutboundBuffer中的所有数据
        // 核心组件：ChannelHandlerContext
        // 重要的是，只有在所有消息都写入完毕后才执行一次flush()操作。
        // 这会将所有累计的编码后的ByteBuf一次性或分批地发送出去，
        // 从而在服务器端观察到粘包现象。
        ctx.flush();
        System.out.println("客户端已发送 " + messages.length + " 条消息（通过延迟刷新模拟粘包）。");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomMessage msg) throws Exception {
        System.out.println("客户端收到响应 (" + (++counter) + ")：" + msg);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

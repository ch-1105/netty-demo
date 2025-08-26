package com.example.nettydemo.server;

import com.example.nettydemo.common.CustomMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty TCP服务器的业务逻辑处理器。
 * 接收由CustomMessageDecoder解码后的CustomMessage对象。
 */
// 核心组件：ChannelHandler，SimpleChannelInboundHandler
// <> 中的CustomMessage表示这个Handler只处理CustomMessage类型的入站消息。
public class TcpServerHandler extends SimpleChannelInboundHandler<CustomMessage> {
    private int counter = 0; // 用于统计接收到的消息数量
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomMessage msg) throws Exception {
        // 核心组件：CustomMessage
        // 我们接收到的msg已经是完整的CustomMessage对象，不再需要担心粘包/拆包。
        System.out.println("服务器收到消息 (" + (++counter) + ")：" + msg);
        // 服务器可以决定是否回写消息，这里只是简单打印
        // 如果要回写，可以调用 ctx.writeAndFlush(new CustomMessage("Server Response: " + msg.getContent()));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // 发生异常时关闭连接
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端 " + ctx.channel().remoteAddress() + " 已连接。");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端 " + ctx.channel().remoteAddress() + " 已断开。");
    }
}
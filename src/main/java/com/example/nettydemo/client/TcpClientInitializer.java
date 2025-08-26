package com.example.nettydemo.client;

import com.example.nettydemo.common.CustomMessageDecoder;
import com.example.nettydemo.common.CustomMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty TCP客户端的Channel初始化器。
 * 当客户端Channel建立时，负责向其ChannelPipeline添加编解码器和业务处理器。
 */
public class TcpClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // (1) 添加日志处理器，方便观察数据流动
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        // (2) 客户端也需要LengthFieldBasedFrameDecoder来处理服务器可能发来的数据
        // (虽然本示例服务器不发数据，但出于完整性考虑添加)
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                1024 * 1024, // 最大帧长度
                0,           // 长度字段偏移量
                4,           // 长度字段字节数
                0,           // 长度调整值
                4            // 剥离长度字段本身
        ));
        // (3) 添加自定义消息解码器，将ByteBuf转换为CustomMessage对象
        pipeline.addLast(new CustomMessageDecoder());
        // (4) 添加自定义消息编码器，将CustomMessage对象转换为ByteBuf
        // 客户端需要这个来编码要发送的CustomMessage
        pipeline.addLast(new CustomMessageEncoder());
        // (5) 添加客户端业务逻辑处理器
        pipeline.addLast(new TcpClientHandler());
    }
}
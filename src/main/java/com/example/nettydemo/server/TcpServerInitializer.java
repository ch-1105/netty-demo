package com.example.nettydemo.server;

import com.example.nettydemo.common.CustomMessageDecoder;
import com.example.nettydemo.common.CustomMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Netty TCP服务器的Channel初始化器。
 * 当一个新的客户端连接建立时，负责向其ChannelPipeline添加编解码器和业务处理器。
 */
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // (1) 添加日志处理器，方便观察数据流动
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        // (2) 添加LengthFieldBasedFrameDecoder，解决粘包/拆包问题
        // 参数解释：
        // maxFrameLength: 1024 * 1024 (1M): 允许的最大消息帧长度，防止恶意超长消息导致OOM。
        // lengthFieldOffset: 0: 长度字段在整个数据包中的偏移量，这里是0，表示长度字段是消息的第一个字节。
        // lengthFieldLength: 4: 长度字段本身的字节长度，这里是4字节（int类型）。
        // lengthAdjustment: 0: 长度字段的值减去这个值，就是消息体的实际长度。
        //                   这里我们约定长度字段的值就是消息体的长度，所以不需要调整。
        // initialBytesToStrip: 4: 从解码后的消息帧中剥离的字节数。
        //                      这里我们希望业务Handler只接收到消息内容（字符串），
        //                      所以需要剥离掉消息头的4字节长度字段。
        // 核心组件：LengthFieldBasedFrameDecoder
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                1024 * 1024, // 最大帧长度
                0,           // 长度字段偏移量
                4,           // 长度字段字节数 (int)
                0,           // 长度调整值
                4            // 剥离长度字段本身
        ));
        // (3) 添加自定义消息解码器，将ByteBuf转换为CustomMessage对象
        // 核心组件：ChannelHandler (CustomMessageDecoder)
        // 这个解码器会在LengthFieldBasedFrameDecoder成功解析出一个完整的消息体（ByteBuf）后被调用。
        pipeline.addLast(new CustomMessageDecoder()); // ByteBuf -> CustomMessage
        // (4) 添加自定义消息编码器，将CustomMessage对象转换为ByteBuf
        // 核心组件：ChannelHandler (CustomMessageEncoder)
        // 虽然本示例客户端只发数据，服务器只收数据，暂时用不到，但为了完整性先加上。
        // 如果服务器需要向客户端发送CustomMessage，则Encoder会起作用。
        pipeline.addLast(new CustomMessageEncoder()); // CustomMessage -> ByteBuf
        // (5) 添加服务器业务逻辑处理器
        // 核心组件：ChannelHandler (TcpServerHandler)
        pipeline.addLast(new TcpServerHandler()); // 处理CustomMessage
    }
}

package com.example.nettydemo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty TCP客户端启动类。
 * 用于演示发送粘包数据给服务器。
 */
public class TcpClient {
    private final String host;
    private final int port;
    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(); // 客户端通常只需要一个EventLoopGroup
        try {
            // (1) 创建Bootstrap实例
            // 核心组件：Bootstrap
            // Bootstrap 是用于启动客户端的辅助类。
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class) // (2) 设置客户端Channel类型：NioSocketChannel
                    .handler(new TcpClientInitializer()); // (3) 配置Channel的处理器
            // (4) 异步连接到服务器
            System.out.println("正在连接到服务器 " + host + ":" + port + "...");
            ChannelFuture future = bootstrap.connect(host, port).sync(); // 连接操作是异步的，sync()阻塞等待连接成功
            System.out.println("已连接到服务器 " + host + ":" + port);
            // (5) 等待Channel关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            System.out.println("Netty TCP客户端已关闭。");
        }
    }
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8088;
        new TcpClient(host, port).run();
    }
}

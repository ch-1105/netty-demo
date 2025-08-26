package com.example.nettydemo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty TCP服务器启动类。
 * 用于演示粘包/拆包问题的解决方案。
 */
public class TcpServer {
    private final int port;
    public TcpServer(int port) {
        this.port = port;
    }
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理I/O数据
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TcpServerInitializer()); // 配置子Channel的处理器
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("Netty TCP服务器已启动，监听端口：" + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("Netty TCP服务器已关闭。");
        }
    }
    public static void main(String[] args) throws Exception {
        int port = 8088; // 使用一个不同于HTTP的端口
        new TcpServer(port).run();
    }
}

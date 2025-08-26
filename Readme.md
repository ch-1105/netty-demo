# Netty 粘包/拆包解决方案 Demo

这是一个使用 Netty 框架演示如何解决 TCP 粘包和拆包问题的简单示例项目。通过构建一个自定义协议的 TCP 客户端和服务器，客户端会故意发送粘包数据，而服务器端则会通过 Netty 提供的 `LengthFieldBasedFrameDecoder` 确保正确地解析出每一条独立的消息。

## 目录

1.  [项目概述](#1-项目概述)
2.  [核心问题：TCP 粘包/拆包](#2-核心问题tcp-粘包拆包)
3.  [解决方案：`LengthFieldBasedFrameDecoder`](#3-解决方案lengthfieldbasedframedecoder)
4.  [项目结构](#4-项目结构)

## 1. 项目概述

本项目旨在：
*   演示 TCP 协议在传输时可能出现的粘包/拆包现象。
*   展示 Netty 如何优雅地使用 `*LengthFieldBasedFrameDecoder*` 来解决这一问题。
*   提供一个简洁、可运行的 Netty TCP 通信示例。

## 2. 核心问题：TCP 粘包/拆包

TCP 是一个面向字节流的协议，发送端应用层写入的数据和接收端应用层读取的数据之间没有明确的边界。这意味着：
*   **粘包**：发送端发送的多个小数据包可能会合并成一个大数据包在接收端一起被读取。
*   **半包/拆包**：发送端发送的一个大数据包可能会被拆分成多个小数据包在接收端分多次被读取。

这些现象会导致接收端无法准确区分每条完整的应用层消息，从而引发业务逻辑错误。

## 3. 解决方案：`LengthFieldBasedFrameDecoder`

Netty 提供了多种解决方案，本项目重点演示 `*LengthFieldBasedFrameDecoder*`。
`LengthFieldBasedFrameDecoder` 的核心思想是：**在每条消息的头部添加一个固定长度的字段，用于指示后续消息体的准确长度。** 它根据这个长度字段切割出完整的消息帧，完美处理粘包和拆包。

它的主要参数包括：
*   `maxFrameLength`：允许的最大消息帧长度，防止 *内存溢出 (OOM)*。
*   `lengthFieldOffset`：长度字段在整个消息帧中的起始偏移量。
*   `lengthFieldLength`：长度字段本身的字节长度（例如 `int` 为 4 字节）。
*   `lengthAdjustment`：长度字段的值与实际消息体长度的调整值。
*   `initialBytesToStrip`：从解码后的消息帧中剥离（丢弃）的起始字节数。

在本示例中，我们约定消息格式为：`[4字节的消息内容长度 (int)]` + `[N字节的消息内容 (UTF-8 字符串)]`。

## 4. 项目结构

```text
src
└── main
    ├── java
    │   └── com
    │       └── example
    │           └── nettydemo
    │               ├── NettyDemoApplication.java
    │               ├── client
    │               │   ├── TcpClient.java
    │               │   ├── TcpClientHandler.java
    │               │   └── TcpClientInitializer.java
    │               ├── common
    │               │   ├── CustomMessage.java
    │               │   ├── CustomMessageDecoder.java
    │               │   └── CustomMessageEncoder.java
    │               └── server
    │                   ├── TcpServer.java
    │                   ├── TcpServerHandler.java
    │                   └── TcpServerInitializer.java
    └── resources
        └── application.properties
```


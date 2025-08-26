package com.example.nettydemo.common;

/**
 * 这是一个简单的自定义消息对象，用于演示粘包/拆包处理。
 * 消息结构：
 * - 4字节：消息内容的长度 (int)
 * - N字节：消息内容 (UTF-8编码的字符串)
 */
public class CustomMessage {
    private String content; // 消息内容
    public CustomMessage(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public String toString() {
        return "CustomMessage{" +
                "content='" + content + '\'' +
                '}';
    }
}

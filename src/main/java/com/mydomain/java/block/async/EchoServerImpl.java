package com.mydomain.java.block.async;

import com.mydomain.EchoServer;

import java.io.IOException;

public class EchoServerImpl implements EchoServer {
    /**
     * 启动服务
     *
     * @param port 端口
     * @throws IOException 异常
     */
    @Override
    public void start(int port) throws IOException {
        // 没有阻塞异步模式
    }
}

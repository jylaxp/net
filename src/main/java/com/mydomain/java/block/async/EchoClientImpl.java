package com.mydomain.java.block.async;

import com.mydomain.EchoClient;

import java.io.IOException;

public class EchoClientImpl implements EchoClient {
    /**
     * 启动客户端
     *
     * @param host 服务器主机
     * @param port 服务器端口
     * @throws IOException 异常
     */
    @Override
    public void start(String host, int port) throws IOException {
        // 没有阻塞异步模式
    }
}

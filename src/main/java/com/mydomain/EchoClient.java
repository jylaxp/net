package com.mydomain;

import java.io.IOException;

/**
 * echo客户端
 */
public interface EchoClient {
    /**
     * 启动客户端
     *
     * @param host 服务器主机
     * @param port 服务器端口
     * @throws IOException 异常
     */
    void start(String host, int port) throws IOException;
}

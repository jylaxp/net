package com.mydomain;

import java.io.IOException;

/**
 * Echo服务端
 */
public interface EchoServer {

    /**
     * 启动服务
     *
     * @param port 端口
     * @throws IOException 异常
     */
    void start(int port) throws IOException;
}

package com.mydomain.java.block.sync;

import com.mydomain.EchoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

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
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = "hello";
        writer.write(msg + System.getProperty("line.separator"));
        writer.flush();
        System.out.println(reader.readLine());
        writer.write("quit");
        writer.flush();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            EchoClient client = new EchoClientImpl();
            client.start("127.0.0.1", 4567);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

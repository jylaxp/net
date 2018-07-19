package com.mydomain.java.nonblock.sync;

import com.mydomain.EchoServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class EchoServerImpl implements EchoServer {

    private Selector selector;

    private volatile boolean running = true;

    /**
     * 启动服务
     *
     * @param port 端口
     * @throws IOException 异常
     */
    @Override
    public void start(int port) throws IOException {
        selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(port), 1024);
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (running) {
            selector.select(5 * 1000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                continue;
            }

            for (SelectionKey key : keys) {
                handle(key);
            }
        }
    }

    private void handle(SelectionKey key) throws IOException {
        if (!key.isValid()) {
            return;
        }

        if (key.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel != null) {
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
        }

        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int size = socketChannel.read(byteBuffer);
            if (size > 0) {
                byteBuffer.flip();
                byte[] data = new byte[byteBuffer.remaining()];
                byteBuffer.get(data);
                String msg = new String(data);
                System.out.println(msg);

                // send
                data = msg.getBytes();
                ByteBuffer wd = ByteBuffer.allocate(data.length);
                wd.clear();
                wd.put(data);
                wd.flip();
                while (wd.hasRemaining()){
                    socketChannel.write(wd);
                }
            } else if (size < 0) {
                key.cancel();
                socketChannel.close();
            }
        }
    }

    public static void main(String[] args) {
        EchoServerImpl echoServer = new EchoServerImpl();
        try {
            echoServer.start(4567);
        } catch (Exception e) {
            echoServer.running = false;
            e.printStackTrace();
        }
    }
}

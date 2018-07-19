package com.mydomain.java.nonblock.sync;

import com.mydomain.EchoClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EchoClientImpl implements EchoClient {

    private Selector selector;
    private SocketChannel channel;

    private volatile boolean running = true;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String host;
    private int port;

    /**
     * 启动客户端
     *
     * @param host 服务器主机
     * @param port 服务器端口
     * @throws IOException 异常
     */
    @Override
    public void start(String host, int port) throws IOException {
        try {

            this.host = host;
            this.port = port;
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);


            new Thread(new Reader()).start();
            countDownLatch.await();
            for (int i = 0; i < 10; i++) {
                String msg = "this is an echo testing " + i;
                byte[] data = msg.getBytes();
                ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
                byteBuffer.put(data);
                byteBuffer.flip();
                channel.write(byteBuffer);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            running = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            try {
                if (!channel.connect(new InetSocketAddress(host, port))) {
                    channel.register(selector, SelectionKey.OP_CONNECT);
                } else {
                    if (channel.finishConnect()) {
                        channel.register(selector, SelectionKey.OP_READ);
                        countDownLatch.countDown();
                    }
                }

                while (running) {
                    selector.select(2 * 1000);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    if (keys.isEmpty()) {
                        continue;
                    }

                    for (SelectionKey key : keys) {
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isConnectable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            if (socketChannel.finishConnect()) {
                                socketChannel.register(selector, SelectionKey.OP_READ);
                                countDownLatch.countDown();
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
                            } else if (size < 0) {
                                key.cancel();
                                socketChannel.close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        EchoClientImpl echoClient = new EchoClientImpl();
        try {
            echoClient.start("127.0.0.1", 4567);
        } catch (Exception e) {
            echoClient.running = false;
            e.printStackTrace();
        }
    }
}

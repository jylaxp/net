package com.mydomain.java.block.sync;

import com.mydomain.EchoServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServerImpl implements EchoServer {
    /**
     * 启动服务
     *
     * @param port 端口
     * @throws IOException 异常
     */
    @Override
    public void start(int port) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new EchoTask(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    private class EchoTask implements Runnable {
        private Socket socket;

        public EchoTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                while (true) {
                    String line = reader.readLine();
                    System.out.println(line);
                    if (line.equalsIgnoreCase("quit")) {
                        break;
                    }

                    writer.write(line + System.getProperty("line.separator"));
                    writer.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            EchoServer server = new EchoServerImpl();
            server.start(4567);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

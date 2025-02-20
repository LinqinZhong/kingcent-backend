package com.kingcent.cabble.server;

import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.CableMessageHead;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CableServer {

    private final ConnectionPool connectionPool = ConnectionPool.getInstance();

    private final int port;

    public CableServer(int port){
        this.port = port;
    }

    public void start(){
        new Thread(() -> {
            ServerSocket innerSocket;
            try {
                innerSocket = new ServerSocket(port);
                while (!innerSocket.isClosed()){
                    try {
                        Socket client = innerSocket.accept();
                        onClient(client);
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                    } finally {
                        // TODO 关闭被p2服务的client
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void onClient(Socket client) {
        try{
            InputStream inputStream = client.getInputStream();
            // 先读取1kb数据，用于判断
            byte[] bytes = SocketUtil.readSync(inputStream, 1024);
            // 转发信息
            String clientHost = client.getInetAddress().getHostAddress();
            int clientPort = client.getPort();
            String serverHost = "192.168.22.219";
            int serverPort = 8089;
            // 申请服务
            connectionPool.useP2(clientHost,port,"shop", new P2Handler() {
                @Override
                public void onServiceNotFound() {
                    System.out.println("服务未找到");
                }

                @Override
                public void onServiceBusy() {
                    System.out.println("服务忙");
                }

                @Override
                public void onReply(byte[] data){
                    System.out.println("回复"+ Arrays.toString(data));
                }

                @Override
                public void onServiceEnd(){
                    System.out.println("服务结束");
                }

                @Override
                public void onServiceProvide(Socket p2) {
                    System.out.println("接入服务");
                    try {
                        // 获取输出流
                        OutputStream outputStream = p2.getOutputStream();
                        // 将最先的数据发送出去
                        outputStream.write(
                                new CableMessage(
                                    CableMessageHead.forward(bytes.length,clientHost,clientPort,serverHost,serverPort),
                                    bytes
                                ).getBytes()
                        );
                        System.out.println("转发");
                        System.out.println(new String(bytes));
                        // 转发任务
                        Thread replyTask = new Thread(() -> {
                            try {
                                // 同步读取剩余的数据
                                System.out.println("继续");
                                SocketUtil.read(inputStream, data -> {
                                    try {
                                        // 发送剩余的数据
                                        outputStream.write(
                                                new CableMessage(
                                                        CableMessageHead.forward(data.length,clientHost,clientPort,serverHost,serverPort),
                                                        data
                                                ).getBytes()
                                        );
                                        System.out.println("转发");
                                        System.out.println(new String(data));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        replyTask.start();
                        replyTask.join();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }catch (IOException e) {
            try {
                client.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

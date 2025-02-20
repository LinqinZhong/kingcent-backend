package com.kingcent.cabble.server;

import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.CableMessageHead;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
            // 先读取1kb数据，用于判断
            byte[] bytes = SocketUtil.readSync(client, 1024);
            if(bytes == null){
                // 读取失败，关闭客户端
                client.close();
                return;
            }
            // 转发信息
            String clientHost = client.getInetAddress().getHostAddress();
            int clientPort = client.getPort();
            String serverHost = "192.168.22.219";
            int serverPort = 8089;
            // 申请服务
            connectionPool.useP2("shop", new P2Handler() {
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
                        // 回复任务
                        Thread replyTask = new Thread(() -> {
                            try {
                                System.out.println("继续读");
                                SocketUtil.read(client, data -> {
                                    System.out.println("转发");
                                    System.out.println(new String(data));
                                    try {
                                        outputStream.write(data);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        replyTask.start();
                        // 同步读取数据
                        SocketUtil.read(client, data -> {
//                            try {
//                                outputStream.write(data);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
                        });
                        // 等待回复任务结束再终止服务
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

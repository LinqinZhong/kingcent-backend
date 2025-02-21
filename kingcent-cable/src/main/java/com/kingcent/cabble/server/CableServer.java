package com.kingcent.cabble.server;

import com.kingcent.cabble.server.messge.CableMessage;
import com.kingcent.cabble.server.messge.CableMessageHead;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
        Logger.info("on client "+client.getInetAddress().getHostAddress()+":"+client.getPort());
        new Thread(() -> {
            try{
                // 先读取1kb数据，用于判断
                byte[] bytes = SocketUtil.readSync(client, 200);
                if(bytes == null){
                    // 读取失败，关闭客户端
                    client.close();
                    return;
                }
                Logger.info("读到"+bytes.length+"字节");
                // 转发信息
                String clientHost = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                String serverHost = "192.168.22.219";
                int serverPort = 8089;
                // 申请服务
                connectionPool.useP2(clientHost,clientPort,"shop", new P2Handler() {
                    @Override
                    public void onServiceNotFound() {
                        try {
                            client.close();
                        } catch (IOException e){
                            e.printStackTrace(System.out);
                        }
                    }

                    @Override
                    public void onServiceBusy() {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }

                    @Override
                    public void onReply(byte[] data){
                        try {
                            OutputStream out = client.getOutputStream();
                            out.write(data);
                        } catch (IOException e) {
                            System.out.println("回复失败:"+e.getMessage());
                        }
                    }

                    @Override
                    public void onServerClosed(){
                        Logger.info("服务端关闭"+client.getInetAddress()+";"+client.getPort());
                        if(!client.isClosed()){
                            try {
                                client.close();
                            } catch (IOException e) {
                                System.out.println("关闭失败"+e);
                            }
                        }
                    }

                    @Override
                    public void onServiceProvide(Socket p2) {
                        Logger.info("接入服务");
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
                            // 转发任务
                            SocketUtil.read(client, data -> {
                                outputStream.write(
                                        new CableMessage(
                                                CableMessageHead.forward(data.length,clientHost,clientPort,serverHost,serverPort),
                                                data
                                        ).getBytes()
                                );
                            });
                        } catch (SocketException e) {
                            if(e.getMessage().equals("Socket closed")){
                                Logger.info("自己");
                            }
                            Logger.info("关闭");
                        }
                        catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }

                    @Override
                    public void onServiceReadyToEnd() {
                    }
                });
            }catch (IOException e) {
                try {
                    client.close();
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }
                e.printStackTrace(System.out);
            }
        }).start();
    }
}

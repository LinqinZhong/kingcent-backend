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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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
        System.out.println("on client "+client.getInetAddress().getHostAddress()+":"+client.getPort());
        AtomicBoolean closeable = new AtomicBoolean(false);
        new Thread(() -> {
            try{
                // 正在执行的线程任务
                List<Thread> taskList = new CopyOnWriteArrayList<>();
                final Thread[] closeTask = {null};
                // 先读取1kb数据，用于判断
                byte[] bytes = SocketUtil.readSync(client, 200);
                if(bytes == null){
                    // 读取失败，关闭客户端
                    client.close();
                    return;
                }
                System.out.println("读到"+bytes.length+"字节");
                // 转发信息
                String clientHost = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                String serverHost = "192.168.22.219";
                int serverPort = 3000;
                // 申请服务
                connectionPool.useP2(clientHost,clientPort,"shop", new P2Handler() {
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
                        try {
                            client.getOutputStream().write(data);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
//                        replayTask.start();
                        System.out.println("转发");
//                        taskList.add(replayTask);
                    }

                    @Override
                    public void onServerClosed(){
                        System.out.println("服务端关闭");
                        // TODO 可能性能。。
                        new Thread(() -> {
                            System.out.println("等待发送完成");
                            // TODO 整合
                            for (Thread thread : taskList) {
                                try {
                                    thread.join();
                                    System.out.println("222......????");
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            System.out.println("关闭");
                            try {
                                client.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            closeable.set(true);
                        }).start();
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
                            System.out.println(new String(bytes));
                            // 转发任务
                            Thread forwardTask = new Thread(() -> {
                                try {
                                    SocketUtil.read(client, data -> {
                                        System.out.println(new String(data));
                                        outputStream.write(
                                                new CableMessage(
                                                        CableMessageHead.forward(data.length,clientHost,clientPort,serverHost,serverPort),
                                                        data
                                                ).getBytes()
                                        );
                                    });
                                } catch (IOException e) {
                                    if(e.getMessage().equals("Socket closed")){
                                        System.out.println("自己");
                                    }
                                    System.out.println("关闭");
                                }
                            });
                            forwardTask.start();
                            //
                            forwardTask.join();
                        } catch (SocketException e) {
                            if(e.getMessage().equals("Socket closed")){
                                System.out.println("自己");
                            }
                            System.out.println("关闭");
                        }
                        catch (IOException e) {
                            e.printStackTrace(System.out);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onServiceReadyToEnd() {
                        System.out.println("开始阻塞");
                        while (!closeable.get()){
                            try {
                                System.out.println("阻塞....................");
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        System.out.println("阻塞完成");
                    }
                });
            }catch (IOException e) {
                try {
                    client.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }
}

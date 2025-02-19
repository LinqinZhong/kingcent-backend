package com.kingcent.net.server;

import com.kingcent.net.*;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class P2 {

    public static void main(String[] args) throws IOException {
        new P2().connect();
    }


    private final Map<String, Socket> socketMap = new HashMap<>();
    private final Set<String> readingSockets = new HashSet<>();


    private Map<String,Integer> sending = new HashMap<>();

    /**
     * 接收消息
     */
    private void receiveMessage(Socket socket, HandMessageHead head, MessageFromP1Handler messageHandler) throws IOException {
        byte[] buffer = new byte[head.getLength()];
        int read = socket.getInputStream().read(buffer);
        if(read == -1){
            System.out.println("接收消息失败");
            // 发送一个关闭报文给P1，通知其强制关闭和client的连接
            // TODO
        }
        try {
            messageHandler.onHandMessage(head, buffer, new Response() {
                @Override
                public void send(byte[] data) throws IOException {
                    sending.put(head.clientName, sending.getOrDefault(head.clientName,0)+1);
                    // 回复
                    HandMessageHead handMessageHead = new HandMessageHead(
                            head.getUuid(),
                            data.length,
                            head.host,
                            head.port,
                            HandMessageHead.Type.TYPE_RESPONSE,
                            head.getClientName()
                    );
                    OutputStream outputStream = socket.getOutputStream();
                    System.out.println("发送");
                    byte[] bytes = DataUtil.concatBytes(handMessageHead.getBytes(), data);
                    outputStream.write(bytes);
                    sending.put(head.clientName, sending.getOrDefault(head.clientName,0)-1);
                    System.out.println("send:"+sending.get(head.clientName));
                }

                @Override
                public void close() {
//                    System.out.println("关闭");
//                    try{
//                        OutputStream outputStream = socket.getOutputStream();
//                        outputStream.write(new HandMessageHead(
//                                head.getUuid(),
//                                0,
//                                head.host,
//                                head.port,
//                                HandMessageHead.Type.SERVER_CLOSE,
//                                head.clientName
//                        ).getBytes());
//                    }catch (IOException ex) {
//                        throw new RuntimeException(ex);
//                    }
                }

                @Override
                public void await(HandMessageHead head) {
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void watch(Socket socket, MessageFromP1Handler messageHandler){
        System.out.println("开始监听");
        new Thread(() -> {
            while (!socket.isClosed()){
                try {
                    byte[] data = new byte[HandMessageHead.SIZE];
                    int read = socket.getInputStream().read(data);
                    if(read == -1) {
                        break;
                    }
                    HandMessageHead handMessageHead = HandMessageHead.parseHead(data);
                    if(handMessageHead != null){
                        receiveMessage(socket,handMessageHead, messageHandler);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        while (!socket.isClosed());
    }

    /**
     * 启动
     */
    public void connect() throws IOException {
        Logger.logo();
        Socket socket = new Socket("119.29.76.76", 10000);
        // 发送连接请求数据包
        HandMessageHead handMessageHead = new HandMessageHead(
                UUID.randomUUID(),
                0,
                "www.baidu.com",
                0,
                HandMessageHead.Type.TYPE_HELLO,
                "0"
        );
        socket.getOutputStream().write(handMessageHead.getBytes());
        Logger.greenBold("P2 is running.");

        watch(socket, new MessageFromP1Handler() {
            @Override
            public void onHandMessage(HandMessageHead head, byte[] data, Response response) {
                String endPoint = head.getHost()+":"+head.getPort();
                System.out.println("------ 转发["+head.uuid+"] -----------");
                System.out.println(head.clientName +"->"+endPoint);
                Socket server = socketMap.computeIfAbsent(head.clientName, (r) -> {
                    try {
                        return new Socket(head.getHost(), 8089);
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                        return null;
                    }
                });
                try {
                    assert server != null;
                    OutputStream outputStream = server.getOutputStream();
                    outputStream.write(data);
                    System.out.println("======================================");
                }catch (IOException e){
                    socketMap.remove(head.clientName);
                    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                }

                if(!readingSockets.contains(head.clientName)){
                    Logger.green("Create new request for "+head.clientName);
                    readingSockets.add(head.clientName);
                    // 回复第一个数据包绑定的P1
                    new Thread(() -> {
                        try{
                            InputStream inputStream = server.getInputStream();
                            while (true) {
                                byte[] buffer = new byte[1024];
                                int len = inputStream.read(buffer);
                                if(len == -1) throw new IOException("关闭");
                                byte[] d = new byte[len];
                                System.arraycopy(buffer, 0, d, 0, len);
                                System.out.println("------ 回复["+head.uuid+"] -----------");
                                System.out.println(endPoint+"->"+head.clientName);
//                                DataUtil.displayBytes(d);
                                System.out.println("======================================");
                                response.send(d);
                            }
                        }catch (IOException e){
                            Logger.blue("服务器断开："+server.getInetAddress());
                            System.out.println(endPoint+"->"+head.clientName);
                            response.await(head);
                            response.close();
                        } finally {
                            readingSockets.remove(head.clientName);
                        }
                    }).start();
                }
            }

            @Override
            public void onClientClose() {

            }
        });
    }
}

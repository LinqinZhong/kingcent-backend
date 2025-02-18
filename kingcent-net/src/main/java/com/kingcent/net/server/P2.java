package com.kingcent.net.server;

import com.kingcent.net.HandMessageHead;
import com.kingcent.net.MessageHandler;
import com.kingcent.net.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class P2 {

    public static void main(String[] args) throws IOException {
        new P2().start();
    }


    private final Map<String, Socket> socketMap = new HashMap<>();
    private final Set<String> readingSockets = new HashSet<>();


    /**
     * 接收消息
     */
    private void receiveMessage(Socket socket, HandMessageHead head, MessageHandler messageHandler) throws IOException {
        byte[] buffer = new byte[head.getLength()];
        int read = socket.getInputStream().read(buffer);
        if(read == -1){
            System.out.println("接收消息失败");
            // 发送一个关闭报文给P1，通知其强制关闭和client的连接
            // TODO
        }
        try {
            messageHandler.onHandMessage(head, buffer, (byte[] data) -> {
                // 回复
                HandMessageHead handMessageHead = new HandMessageHead(
                        head.getUuid(),
                        data.length,
                        head.host,
                        head.port,
                        HandMessageHead.TYPE_RESPONSE,
                        head.getClientName()
                );
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(handMessageHead.getBytes());
                outputStream.write(data);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void watch(Socket socket, MessageHandler messageHandler){
        System.out.println("开始监听");
        new Thread(() -> {
            while (!socket.isClosed()){
                try {
                    byte[] data = new byte[HandMessageHead.SIZE];
                    int read = socket.getInputStream().read(data);
                    if(read == -1) {
                        System.out.println("服务关闭");
                        break;
                    }
                    HandMessageHead handMessageHead = HandMessageHead.parseHead(data);
                    if(handMessageHead != null){
                        receiveMessage(socket,handMessageHead, messageHandler);
                    };
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * 启动
     */
    public void start() throws IOException {
        Socket socket = new Socket("localhost", 10000);
        // 发送连接报文
        HandMessageHead handMessageHead = new HandMessageHead(
                UUID.randomUUID(),
                0,
                "www.baidu.com",
                0,
                HandMessageHead.TYPE_HELLO,
                "0"
        );
        socket.getOutputStream().write(handMessageHead.getBytes());

        watch(socket, new MessageHandler() {
            @Override
            public void onHandMessage(HandMessageHead head, byte[] data, Response response) throws IOException {
                String endPoint = head.getHost()+":"+head.getPort();
                System.out.println("------ 转发["+head.uuid+"] -----------");
                System.out.println(head.clientName +"->"+endPoint);
//                System.out.println(new String(data));
                Socket localhost = socketMap.computeIfAbsent(head.clientName, (r) -> {
                    try {
                        return new Socket(head.getHost(),head.getPort() );
                    } catch (IOException e) {
                        return null;
                    }
                });
                OutputStream outputStream = localhost.getOutputStream();
                outputStream.write(data);
                System.out.println("======================================");

                if(!readingSockets.contains(head.clientName)){
                    System.out.println("创建新的访问");
                    readingSockets.add(head.clientName);
                    // 回复第一个数据包绑定的P1
                    new Thread(() -> {
                        try{
                            InputStream inputStream = localhost.getInputStream();
                            while (true) {
                                byte[] buffer = new byte[10240];
                                int len = inputStream.read(buffer);
                                if(len == -1) break;
                                byte[] d = new byte[len];
                                System.arraycopy(buffer, 0, d, 0, len);
                                System.out.println("------ 回复["+head.uuid+"] -----------");
                                System.out.println(endPoint+"->"+head.clientName);
//                                System.out.println(new String(d));
                                System.out.println("======================================");
                                response.send(d);
                            }
                            readingSockets.remove(head.clientName);
                        }catch (IOException e){

                        }
                    }).start();
                }
            }

            @Override
            public void onMessage(byte[] data) {

            }

            @Override
            public void onP2(String ip, Socket socket) {

            }
        });
    }
}

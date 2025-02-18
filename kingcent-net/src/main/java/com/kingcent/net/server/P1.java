package com.kingcent.net.server;

import com.kingcent.net.HandMessageHead;
import com.kingcent.net.MessageHandler;
import com.kingcent.net.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class P1 {

    private static final Map<String,Socket> p2Map = new HashMap<>();

    private static final Map<Socket, HandMessageHead> isReadyToReceive = new HashMap<>();

    private static final Map<UUID, Socket> waitResponseSocket = new HashMap<>();

    private static String getAddress(Socket socket){
        return socket.getInetAddress().getHostName()+";"+socket.getPort();
    }

    public static void main(String[] args) throws IOException {
        try (ServerSocket socket = new ServerSocket(10000)) {
            while (true) {
                Socket accept = socket.accept();
                System.out.println("接入->" + getAddress(accept));
                watch(accept, new MessageHandler() {
                    @Override
                    public void onHandMessage(HandMessageHead head, byte[] data, Response response) throws IOException {
                        Socket socket = waitResponseSocket.get(head.uuid);
                        if (socket != null) {
                            System.out.println("------ 回复[" + head.uuid + "] -----------");
                            String from = getAddress(accept);
                            String to = getAddress(socket);
                            System.out.println(from + "->" + to);
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(data);
                            System.out.println("======================================");
                        }
                    }

                    @Override
                    public void onMessage(byte[] data) {
                        UUID uuid = UUID.randomUUID();
                        System.out.println("------ 转发[" + uuid + "] -----------");
                        Socket p2 = p2Map.get("www.baidu.com");
                        String from = getAddress(accept);
                        String to = getAddress(p2);
                        System.out.println(from + "->" + to);
                        waitResponseSocket.put(uuid, accept);
                        request(from, uuid, p2, data);
                        System.out.println("======================================");
                    }

                    @Override
                    public void onP2(String ip, Socket socket) {
                        p2Map.put(ip, socket);
                    }
                });
            }
        }
    }

    private static void watch(Socket socket, MessageHandler messageHandler){
        new Thread(() -> {
            while (!socket.isClosed()){
                try {
                    if(isReadyToReceive.containsKey(socket)){

                        HandMessageHead remove = isReadyToReceive.remove(socket);
                        byte[] buffer = new byte[remove.length];
                        InputStream inputStream = socket.getInputStream();
                        inputStream.read(buffer);
                        messageHandler.onHandMessage(remove, buffer, null);
                        continue;
                    }


                    byte[] buffer = new byte[HandMessageHead.SIZE];
                    InputStream inputStream = socket.getInputStream();
                    int len = inputStream.read(buffer);
                    if(len == -1){
                        break;
                    }
                    byte[] data = new byte[len];
                    System.arraycopy(buffer,0,data,0,len);

                    HandMessageHead handMessageHead = HandMessageHead.parseHead(data);
                    if(handMessageHead != null){
                        switch (handMessageHead.type){
                            case HandMessageHead.TYPE_HELLO -> {
                                messageHandler.onP2(handMessageHead.getHost(),socket);
                            }
                            case HandMessageHead.TYPE_RESPONSE -> {
                                isReadyToReceive.put(socket, handMessageHead);
                            }
                        }
                    }else {
                        messageHandler.onMessage(data);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private static void request(String clientName, UUID uuid, Socket socket, byte[] data) {
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
            HandMessageHead handMessageHead = new HandMessageHead(uuid, data.length, "localhost", 6630, HandMessageHead.TYPE_REQUEST, clientName);
            outputStream.write(handMessageHead.getBytes());
            outputStream.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

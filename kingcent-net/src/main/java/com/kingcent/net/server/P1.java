package com.kingcent.net.server;

import com.kingcent.net.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class P1 {


    private static final ConnectionPool connections = new ConnectionPool();

    private static final Map<UUID, Socket> waitResponseSocket = new HashMap<>();

    private static String getAddress(Socket socket){
        return socket.getInetAddress().getHostAddress()+";"+socket.getPort();
    }


    public static void main(String[] args) throws IOException {
        try (ServerSocket socket = new ServerSocket(10000)) {
            Logger.logo();
            Logger.greenBold("P1 is running.\n\n\n");
            while (!socket.isClosed()) {
                Socket accept = socket.accept();
                System.out.println(">>>" + getAddress(accept));
                watch(accept, new MessageHandler() {
                    @Override
                    public void onHandMessage(HandMessageHead head, byte[] data, Response response) {
                        String from = getAddress(accept);
                        try {
                            Socket socket = waitResponseSocket.get(head.uuid);
                            if (socket != null) {
                                String to = getAddress(socket);
                                Logger.blue("RP[" + head.uuid + "]：" + from + " -> " + to);
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(data);
                                Logger.blue("RP[" + head.uuid + "]: success");
                            }
                        }catch(IOException e){
                            // 客户端主动关闭
                            Logger.red("client closed:"+ from);
                            // 通知P2
                            Socket p2 = connections.getP2OfClient(head.clientName);
                            if(p2 != null)
                                System.out.println("通知"+getAddress(p2));
                        }
                    }

                    @Override
                    public void onMessage(byte[] data) {
                        String hostName = accept.getInetAddress().getHostName();
                        int port = hostName.equals("a.com") ? 8888 : 8089;
                        String from = getAddress(accept);
                        UUID uuid = UUID.randomUUID();
                        try {
                            System.out.println(hostName);
                            String name = "www.baidu.com";
                            Socket p2 = connections.getP2(from, name);
                            if(p2 == null){
                                Logger.red("No such server: "+name);
                                accept.close();
                                return;
                            }
                            String to = getAddress(p2);
                            Logger.yellow("FW[" + uuid + "]："+from + " -> " + to);
                            waitResponseSocket.put(uuid, accept);
                            request(from, "localhost", port, uuid, p2, data);
                            Logger.yellow("FW[" + uuid + "]: success");
                        } catch (InterruptedException e) {
                            Logger.red("FW[" + uuid + "]: failed");
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onP2SayHello(String name, Socket socket) {
                        Logger.greenUnderline("P2["+name+" | "+getAddress(socket)+"] is connected");
                        connections.addP2(name, socket);
                    }

                    @Override
                    public void onServerClose(HandMessageHead head) {
                        try {
                            System.out.println("Server closed: "+head.getHost());
                            waitResponseSocket.remove(head.uuid).close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    }

    private static void watch(Socket socket, MessageHandler messageHandler){
        new Thread(() -> {
            while (!socket.isClosed()){
                try {
                    byte[] buffer = new byte[HandMessageHead.SIZE];
                    InputStream inputStream = socket.getInputStream();
                    int len = inputStream.read(buffer);
                    if(len == -1){
                        break;
                    }
                    byte[] data = new byte[len];
                    System.arraycopy(buffer,0,data,0,len);
                    // 打印所有接收的数据包，调试用
                    // System.out.println(new String(data));
                    HandMessageHead handMessageHead = HandMessageHead.parseHead(data);
                    if(handMessageHead != null){
                        switch (handMessageHead.type){
                            // P2接入
                            case TYPE_HELLO -> messageHandler.onP2SayHello(handMessageHead.getHost(),socket);
                            /*
                               收到一个回复类型的头，下面再收到的必然是长度为head.length的数据
                               那么接下来就要接入head.length字节的数据，并和头一起派发给onHandMessage事件
                            */
                            case TYPE_RESPONSE -> {
                                byte[] realData = new byte[handMessageHead.length];
                                InputStream inputStream1 = socket.getInputStream();
                                inputStream1.read(realData);
                                messageHandler.onHandMessage(handMessageHead, realData, null);
                            }
                            // 服务器关闭
                            case SERVER_CLOSE -> messageHandler.onServerClose(handMessageHead);
                        }
                    }else {
                        messageHandler.onMessage(data);
                    }
                }
                catch (IOException e) {
                    String p2Name = connections.remove(socket);
                    if(p2Name != null){
                        // P2连接异常或断开
                        try{
                            socket.close();
                            Logger.red("P2["+p2Name+" | "+getAddress(socket)+"] is dead.");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else{
                        // 客户端断开
                        System.out.println("Client closed:"+ getAddress(socket));
                    }
                }
            }
        }).start();
    }

    private static void request(String clientName, String destHost, int destPort, UUID uuid, Socket socket, byte[] data) {
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
            HandMessageHead handMessageHead = new HandMessageHead(uuid, data.length, destHost, destPort, HandMessageHead.Type.TYPE_REQUEST, clientName);
            outputStream.write(DataUtil.concatBytes(handMessageHead.getBytes(),data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

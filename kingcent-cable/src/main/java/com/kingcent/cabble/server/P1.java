package com.kingcent.cabble.server;

import com.kingcent.cabble.server.exception.CableMessageException;
import com.kingcent.cabble.server.messge.*;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class P1 {

    private final  ConnectionPool connectionPool = ConnectionPool.getInstance();

    private final int innerPort;

    public P1(int innerPort){
        this.innerPort = innerPort;
    }

    public void start(){
        new Thread(() -> {
            ServerSocket innerSocket;
            try {
                innerSocket = new ServerSocket(innerPort);
                while (!innerSocket.isClosed()){
                    try {
                        Socket p2 = innerSocket.accept();
                        onP2(p2);
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

    private void onP2(Socket p2) {
        new Thread(()->{
            if(buildSafetyConnection(p2)) {
                listen(p2, new CableMessageListener() {
                    @Override
                    public void onForward(CableMessageHead head, byte[] data) {
                        System.out.println(new String(data, StandardCharsets.UTF_8));
                    }

                    @Override
                    public void onOuterClose() {

                    }

                    @Override
                    public void onForwardCompleted() {

                    }

                    @Override
                    public void onListenEnd(){
                        System.out.println("输入关闭");
                    }
                });
            }
        }).start();
    }

    private boolean buildSafetyConnection(Socket p2) {
        HelloInfo helloInfo = requireHelloInfo(p2);
        if (helloInfo == null) return false;
        // TODO 把这块改成配置
        if (helloInfo.getSecret().equals("11223344")) {
            String serviceName = helloInfo.getServiceName();
            connectionPool.addP2(serviceName,p2);
            System.out.println("P2[" + helloInfo.getServiceName() + "][" + p2.getInetAddress().getHostAddress() + ":" + p2.getPort() + "] is connected");
            return true;
        }
        return false;
    }

    // 等待来自p2的招呼
    private HelloInfo requireHelloInfo(Socket p2){
        try {
            InputStream inputStream = p2.getInputStream();
            byte[] bytes = SocketUtil.readSync(inputStream, CableMessageHead.SIZE);
            if (bytes == null) return null;
            CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
            if(cableMessageHead.getType() != CableMessageType.HELLO) return null;
            byte[] body = SocketUtil.readSync(inputStream, cableMessageHead.getLength());
           return HelloInfo.fromBytes(body);
        } catch (CableMessageException | IOException e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    // 监听来自p2的消息
    private void listen(Socket p2, CableMessageListener listener){
        try {
            InputStream inputStream = p2.getInputStream();
            while (!p2.isClosed()){
                byte[] bytes = SocketUtil.readSync(inputStream, CableMessageHead.SIZE);
                if(bytes == null){
                    break;
                }
                CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
                switch (cableMessageHead.getType()){
                    case FORWARD -> {
                        // 接收转发消息边读边回调
                        SocketUtil.read(inputStream,cableMessageHead.getLength(), data -> listener.onForward(cableMessageHead,data));
                        listener.onForwardCompleted();
                    }
                    case OUTER_CLOSE -> {
                        // 关闭
                        listener.onOuterClose();
                    }
                }
            }
            listener.onListenEnd();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CableMessageException e) {
            // TODO p2发了一个错误的头部数据，安全起见，断开与其的连接
        }
    }

}

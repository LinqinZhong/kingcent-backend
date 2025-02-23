package com.kingcent.cabble.server;

import com.kingcent.cabble.server.exception.CableMessageException;
import com.kingcent.cabble.server.messge.*;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
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
                        connectionPool.onAlive(p2);
                        P2Handler handlerOfClient = connectionPool.getHandlerOfClient(head.getClientHost(), head.getClientPort());
                        if (handlerOfClient != null) {
                            handlerOfClient.onReply(data);
                        }
                    }

                    @Override
                    public void onOuterClose(CableMessageHead head) {
                        connectionPool.onAlive(p2);
                        P2Handler handlerOfClient = connectionPool.getHandlerOfClient(head.getClientHost(), head.getClientPort());
                        if (handlerOfClient != null) {
                            handlerOfClient.onServerClosed();
                        }
                    }

                    @Override
                    public void onForwardCompleted(CableMessageHead head) {
                        connectionPool.onAlive(p2);
                        Logger.info("转发完成");
                    }

                    @Override
                    public void onListenEnd(){
                        connectionPool.removeP2(p2);
                    }

                    @Override
                    public void onPingPong() {
                        connectionPool.onAlive(p2);
                        try {
                            p2.getOutputStream().write(CableMessage.pingPong().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                });
            }
        }).start();
    }

    private boolean buildSafetyConnection(Socket p2) {
        try {
            byte[] bytes = SocketUtil.readSync(p2, CableMessageHead.SIZE);
            if (bytes == null) return false;
            CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
            if(cableMessageHead.getType() != CableMessageType.HELLO) return false;
            byte[] body = SocketUtil.readSync(p2, cableMessageHead.getLength());
            HelloInfo helloInfo = HelloInfo.fromBytes(body);
            if (helloInfo == null) return false;
            // TODO 把这块改成配置
            if (helloInfo.getSecret().equals("11223344")) {
                String serviceName = helloInfo.getServiceName();
                connectionPool.addP2(serviceName,p2);
                byte[] ok = "OK".getBytes(StandardCharsets.UTF_8);
                p2.getOutputStream().write(
                        new CableMessage(CableMessageHead.reply(cableMessageHead,ok.length),ok).getBytes()
                );
                Logger.info("P2[" + helloInfo.getServiceName() + "][" + p2.getInetAddress().getHostAddress() + ":" + p2.getPort() + "] is connected");
                return true;
            }
            return false;
        } catch (CableMessageException | IOException e) {
            e.printStackTrace(System.out);
        }
        return false;
    }



    // 监听来自p2的消息
    private void listen(Socket p2, CableMessageListener listener){
        try {
            while (!p2.isClosed()){
                byte[] bytes = SocketUtil.readSync(p2, CableMessageHead.SIZE);
                if(bytes == null){
                    break;
                }
                CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
                switch (cableMessageHead.getType()){
                    case FORWARD -> {
                        // 接收转发消息边读边回调
                        SocketUtil.read(p2,cableMessageHead.getLength(), data -> listener.onForward(cableMessageHead,data));
                        listener.onForwardCompleted(cableMessageHead);
                    }
                    case OUTER_CLOSE -> {
                        // 关闭
                        listener.onOuterClose(cableMessageHead);
                    }
                    case PING_PONG -> {
                        // 心跳
                        listener.onPingPong();
                    }
                }
            }
        } catch (CableMessageException | IOException ignored) {}
        listener.onListenEnd();
    }

}

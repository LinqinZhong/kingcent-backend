package com.kingcent.cabble.server;

import com.kingcent.cabble.server.messge.CableMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {



    private static ConnectionPool instance;

    // P2是否在范围时间内有心跳
    private final Map<Socket,Boolean> isPinged = new ConcurrentHashMap<>();

    // P2列表列表
    private final List<Socket> p2List = new CopyOnWriteArrayList<>();

    // 服务名对应的可用P2列表
    private final Map<String, LinkedBlockingQueue<Socket>> p2OfServices = new ConcurrentHashMap<>();

    private final Map<Socket,LinkedBlockingQueue<Socket>> queueOfP2 = new ConcurrentHashMap<>();

    // 客户端绑定的P2Handler
    private final Map<String,P2Handler> handlerOfClient = new ConcurrentHashMap<>();

    public static ConnectionPool getInstance(){
        ConnectionPool connectionPool = instance == null ? instance = new ConnectionPool() : instance;
        Logger.info(connectionPool+"");
        return connectionPool;
    }

    public ConnectionPool(){
        checkPing();
    }

    public void addP2(String serviceName, Socket p2){
        Logger.info("add-p2: "+serviceName);
        LinkedBlockingQueue<Socket> p2s;
        if(p2OfServices.containsKey(serviceName)){
            p2s = p2OfServices.get(serviceName);
        }else{
            p2s = new LinkedBlockingQueue<>();
            p2OfServices.put(serviceName, p2s);
        }
        p2List.add(p2);
        queueOfP2.put(p2, p2s);
        p2s.add(p2);
    }

    // 获取client绑定的的handler
    public P2Handler getHandlerOfClient(String clientHost, int clientPort){
        return handlerOfClient.get(clientHost+":"+clientPort);
    }

    public void useP2(String clientHost, int clientPort, String serviceName, P2Handler p2Handler){
        LinkedBlockingQueue<Socket> p2s = p2OfServices.get(serviceName);
        if(p2s == null) {
            // 服务未发现
            p2Handler.onServiceNotFound();
            return;
        }
        // 取出一个P2
        Socket p2 = p2s.peek();
        // 记录client绑定的p2
        String clientName = clientHost+":"+clientPort;
        handlerOfClient.put(clientName, p2Handler);
        new Thread(() -> {
            // 为调用者提供服务
            p2Handler.onServiceProvide(p2);
            // 准备结束的时候调用
            p2Handler.onServiceReadyToEnd();
            // 清除p2的服务
            handlerOfClient.remove(clientName);
        }).start();
    }

    public void removeP2(Socket p2) {
        LinkedBlockingQueue<Socket> sockets = queueOfP2.get(p2);
        if(sockets != null) {
            p2List.remove(p2);
            sockets.remove(p2);
        }
    }

    // 检测心跳
    private void checkPing(){
        new Thread(() -> {
            while (true){
                for (Socket p2: p2List) {
                    Boolean isPing = isPinged.get(p2);
                    if(isPing == null || !isPing){
                        System.out.println("P2 is dead");
                        if(!p2.isClosed()){
                            try {
                                p2.close();
                            } catch (IOException e) {
                                e.printStackTrace(System.out);
                            }
                        }
                        removeP2(p2);
                    }
                }
                isPinged.clear();   // 重置
                try {
                    Thread.sleep(Config.PING_PONG_TIME*2);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void onPing(Socket p2) {
        isPinged.put(p2,true);
    }
}

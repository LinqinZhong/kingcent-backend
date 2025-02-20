package com.kingcent.cabble.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionPool {

    private static ConnectionPool instance;

    // 可用的P2列表
    private final Map<String, LinkedBlockingQueue<Socket>> p2OfServices = new HashMap<>();

    // P2正在服务的Handler
    private final Map<Socket,P2Handler> handlersServedByP2 = new HashMap<>();

    public static ConnectionPool getInstance(){
        ConnectionPool connectionPool = instance == null ? instance = new ConnectionPool() : instance;
        System.out.println(connectionPool);
        return connectionPool;
    }

    public void addP2(String serviceName, Socket p2){
        System.out.println("add-p2: "+serviceName);
        LinkedBlockingQueue<Socket> p2s;
        if(p2OfServices.containsKey(serviceName)){
            p2s = p2OfServices.get(serviceName);
        }else{
            p2s = new LinkedBlockingQueue<>();
            p2OfServices.put(serviceName, p2s);
        }
        p2s.add(p2);
    }

    // 获取p2服务的handler
    public P2Handler getHandlerOfP2(Socket p2){
        return handlersServedByP2.get(p2);
    }

    public void useP2(String serviceName, P2Handler p2Handler){
        LinkedBlockingQueue<Socket> p2s = p2OfServices.get(serviceName);
        if(p2s == null) {
            // 服务未发现
            p2Handler.onServiceNotFound();
            return;
        }
        try {
            // 取出一个P2
            Socket p2 = p2s.take();
            // 记录P2正在为当前p2Handler服务
            handlersServedByP2.put(p2, p2Handler);
            new Thread(() -> {
                // 为调用者提供服务
                p2Handler.onServiceProvide(p2);
                // p2重新入队
                try {
                    p2s.put(p2);
                } catch (InterruptedException e) {
                    // 入队失败，关闭p2
                    System.out.println("关闭p2");
                    try {
                        p2.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                // 清除p2的服务
                handlersServedByP2.remove(p2);
            }).start();
        } catch (InterruptedException e) {
            // 阻塞被打断（服务忙）
            p2Handler.onServiceBusy();
        }
    }
}

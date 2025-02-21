package com.kingcent.cabble.server;

import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ServerRequester {
    Map<String, Socket> serverMap = new ConcurrentHashMap<>();

    public void send(String clientName, String host, int port, byte[] data, ServerRequestHandler handler) {
        new Thread(() -> {
            // 接收任务
            AtomicReference<Thread> readingTask = new AtomicReference<>();
            try {
                // 使用ConcurrentHashMap的原子方法，创建server后同时创建接收任务，保证线程安全
                Objects.requireNonNull(serverMap.computeIfAbsent(clientName, (r) -> {
                    try {
                        Socket s = new Socket(host, port);
                        Logger.info("创建=========" + clientName);
                        // 创建接收任务
                        readingTask.set(new Thread(() -> {
                            try {
                                SocketUtil.read(s, handler::onReply);
                            } catch (IOException e) {
                                Logger.info("读：服务端关闭");
                            }
                            handler.onClose();
                            Logger.info("关闭----------------");
                        }));
                        readingTask.get().start();
                        return s;
                    } catch (IOException e) {
                        return null;
                    }
                })).getOutputStream().write(data);
            } catch (IOException e) {
                Logger.info("写：服务端关闭");
            }
            // 发送完成后要阻塞等待接收线程完成
            if(readingTask.get() != null){
                try {
                    readingTask.get().join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}

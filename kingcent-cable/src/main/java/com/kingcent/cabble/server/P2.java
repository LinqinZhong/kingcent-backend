package com.kingcent.cabble.server;

import com.kingcent.cabble.server.exception.CableMessageException;
import com.kingcent.cabble.server.http.HttpHeader;
import com.kingcent.cabble.server.messge.*;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class P2 {

    private final String serviceName;
    private final String serverAddress;
    private final String secret;
    private final int serverPort;
    private Thread starting = null;

    private Thread pingTask = null;

    private Socket p1 = null;

    private int retryTimes = 0;

    private final ServerRequester serverRequester = new ServerRequester();

    // Count of ping unponged.
    private int unpongPing = 0;

    // Whether the p2 has communicated with p1 in current ping-pong time.
    private boolean isCommunicated = false;

    public static void main(String[] args)  {
        new P2("shop","119.29.76.76",8889,"11223344").start();
//        new P2("shop","localhost",8889,"11223344").start();
    }

    public P2(String serviceName, String serverAddress, int serverPort, String secret){
        this.serviceName = serviceName;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.secret = secret;
    }



    public void start(){
        retryTimes  = 0;
        if(starting != null){
            Logger.info("P2 is already started");
            return;
        }
        Logger.info("Starting p2...");
        starting = new Thread(this::handleStart);
        starting.start();
        try {
            starting.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStart() {
        if(p1 != null && !p1.isClosed()) {
            try {
                p1.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Logger.info("Trying connecting to p1.");
            p1 = new Socket(serverAddress,serverPort);
            retryTimes  = 0;
            Logger.info("Connected to p1.");
            if(buildSafetyConnection()){

                startPingTask();
                listen(new CableMessageListener() {
                    @Override
                    public void onForward(CableMessageHead head, byte[] data) {

                        long a = System.currentTimeMillis();
                        HttpHeader httpHeader = new HttpHeader().init(data);
                        if(httpHeader.isHttpRequestHeader()){
                            System.out.println("dasasssssssssssss");
                            data = httpHeader.setHost(head.getServerHost()).getBytes();
                        }

                        System.out.println("-----"+(System.currentTimeMillis() - a));

                        Logger.info("\n=======================================");
                        Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        Logger.info(new String(data, StandardCharsets.UTF_8));;
                        Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        Logger.info("=======================================\n");
                        String clientName = head.getClientHost() + ":" + head.getClientPort();

                        serverRequester.send(clientName, "localhost", head.getServerPort(), data, new ServerRequestHandler() {
                            @Override
                            public void onReply(byte[] data) {
//                                Logger.info("\n=======================================");
//                                Logger.info("+++++++++++++++++++++++++++++++++++++++");
//                                Logger.info(new String(data, StandardCharsets.UTF_8));
//                                Logger.info("+++++++++++++++++++++++++++++++++++++++");
//                                Logger.info("=======================================\n");
                                // 向p1转发回复的消息
                                try {
                                    p1.getOutputStream().write(
                                            new CableMessage(
                                                   CableMessageHead.reply(head,data.length),data
                                            ).getBytes()
                                    );
                                    isCommunicated = true;
                                } catch (IOException e) {
                                    e.printStackTrace(System.out);
                                }
                            }

                            @Override
                            public void onClose() {
                                Logger.info("closed");
                                // 通知p1关闭
                                try {
                                    p1.getOutputStream().write(CableMessage.outerClose(head).getBytes());
                                    isCommunicated = true;
                                } catch (IOException e) {
                                    e.printStackTrace(System.out);
                                }
                            }
                        });
                    }

                    @Override
                    public void onOuterClose(CableMessageHead head) {

                    }

                    @Override
                    public void onForwardCompleted(CableMessageHead head) {

                    }

                    @Override
                    public void onListenEnd() {

                    }

                    @Override
                    public void onPingPong() {
                        System.out.println("pong");
                        unpongPing--;
                    }
                });
            }
        } catch (IOException e) {
            try {
                Thread.sleep(Math.max(retryTimes++* 1000L,20000L));
            } catch (InterruptedException ignored) {}
            Logger.info("Cannot access to p1, retrying("+retryTimes+")...");
            handleStart();
        }
    }

    /**
     * Try to build a safety connection with P1 when connected with P1;
     * The progression like this:
     * 1. The P2 send a HELLO message to p1, which contains the serviceName,secret of p2;
     * 2. When the P1 receive the HELLO message and success to check the authorization of P2, send a HELLO-REPLY message to P2;
     * 3. P2 receive the HELLO-REPLY message from P1;
     * 4. The safety connection is built;
     * @return The result of building the safety connection. (true means success)
     */
    private boolean buildSafetyConnection(){
        CableMessage helloMsg = CableMessage.hello(new HelloInfo(serviceName, secret));
        try {
            p1.getOutputStream().write(helloMsg.getBytes());
            byte[] bytes = SocketUtil.readSync(p1, CableMessageHead.SIZE);
            if(bytes == null){
                Logger.info("Fail to build a safety connection because the connection doesn't send any bytes.");
                return false;
            }
            CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
            if(cableMessageHead.getType() == CableMessageType.HELLO){
                byte[] helloBytes = SocketUtil.readSync(p1, cableMessageHead.getLength());
                if(helloBytes == null){
                    Logger.info("Fail to build a safety connection with p1, connection closed.");
                    return false;
                }
                String helloRlyMsg = new String(helloBytes,StandardCharsets.UTF_8);
                Logger.info("Built a safety connection with p1 successfully.");
                Logger.info(helloRlyMsg);
                return true;
            }else{
                Logger.info("Fail to build a safety connection, reject by p1.");
                return false;
            }
        } catch (IOException | CableMessageException e) {
            Logger.info("Fail to build a safety connection due to the exception: "+e.getMessage()+".");
            return false;
        }
    }

    /**
     * This is a function to listen CableMessage from p1
     * @param listener You should offer a listener then you will catch the callback events of the listening
     */
    private void listen(CableMessageListener listener){
        Logger.info("Listening CableMessage from p1");
        try {
            while (!p1.isClosed()){
                byte[] bytes = SocketUtil.readSync(p1, CableMessageHead.SIZE);
                if(bytes == null) break;
//                // If you want to see what the data p2 received is, release followed component.
//                Logger.info("---------------------");
//                Logger.info(new String(bytes, StandardCharsets.UTF_8));
//                Logger.info("----------------------");
                CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
                switch (cableMessageHead.getType()){
                    case FORWARD -> {
                        SocketUtil.read(p1,cableMessageHead.getLength(),(bodyBytes) -> {
                            listener.onForward(cableMessageHead, bodyBytes);
                        });
                    }
                    case OUTER_CLOSE -> {
                        listener.onOuterClose(cableMessageHead);
                    }
                    case PING_PONG -> {
                        listener.onPingPong();
                    }
                }
            }
        } catch (IOException e) {
        } catch (CableMessageException e) {
            Logger.info("Receive an unknown message:"+ e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Connection is broken, try reconnecting.");
        handleStart();
    }

    private void startPingTask() {
        if(pingTask != null){
            pingTask.stop();
        }
        unpongPing = 0;
        pingTask = new Thread(() -> {
            while (!p1.isClosed()){
                try {
                    Thread.sleep(Config.PING_PONG_TIME);
                } catch (InterruptedException ignored) {}
                if(unpongPing > 10){
                    // Too much fail ping, close p1 and the program will try reconnecting.
                    try {
                        p1.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                try {
                    // If it didn't communicate with P1 with an unclosed state,send a ping message to P1.
                    if(!isCommunicated && !p1.isClosed()) {
                        System.out.println("ping");
                        p1.getOutputStream().write(
                                CableMessage.pingPong().getBytes()
                        );
                        unpongPing++;
                    }
                    isCommunicated = false;
                } catch (IOException e) {
                    return;
                }
            }
        });
        pingTask.start();
    }
}

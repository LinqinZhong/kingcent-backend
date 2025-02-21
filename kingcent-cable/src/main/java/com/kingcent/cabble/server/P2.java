package com.kingcent.cabble.server;

import com.kingcent.cabble.server.exception.CableMessageException;
import com.kingcent.cabble.server.messge.*;
import com.kingcent.cabble.server.utils.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class P2 {

    private final String serviceName;
    private final String serverAddress;
    private final String secret;
    private final int serverPort;
    private Thread starting = null;

    private Socket p1 = null;

    private int retryTimes = 0;

    private final ServerRequester serverRequester = new ServerRequester();

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
            System.out.println("P2 is already started");
            return;
        }
        System.out.println("Starting p2...");
        starting = new Thread(this::handleStart);
        starting.start();
        try {
            starting.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStart() {
        try {
            p1 = new Socket(serverAddress,serverPort);
            retryTimes  = 0;
            System.out.println("Connected to p1.");
            if(buildSafetyConnection()){
                listen(new CableMessageListener() {
                    @Override
                    public void onForward(CableMessageHead head, byte[] data) {
                        System.out.println("\n=======================================");
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        System.out.println(new String(data, StandardCharsets.UTF_8));;
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        System.out.println("=======================================\n");
                        String clientName = head.getClientHost() + ":" + head.getClientPort();
                        serverRequester.send(clientName, head.getServerHost(), head.getServerPort(), data, new ServerRequestHandler() {
                            @Override
                            public void onReply(byte[] data) {
                                System.out.println("\n=======================================");
                                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                                System.out.println(new String(data, StandardCharsets.UTF_8));
                                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                                System.out.println("=======================================\n");
                                // 向p1转发回复的消息
                                try {
                                    p1.getOutputStream().write(
                                            new CableMessage(
                                                   CableMessageHead.reply(head,data.length),data
                                            ).getBytes()
                                    );
                                } catch (IOException e) {
                                    e.printStackTrace(System.out);
                                }
                            }

                            @Override
                            public void onClose() {
                                System.out.println("closed");
                                // 通知p1关闭
                                try {
                                    p1.getOutputStream().write(CableMessage.outerClose(head).getBytes());
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
                });
            }
        } catch (IOException e) {
            try {
                Thread.sleep(retryTimes++* 1000L);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println(p1 == null ? "Cannot access to p1, retrying("+retryTimes+")..." : "Disconnect from p1, try to reconnect("+retryTimes+")...");
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
                System.out.println("Fail to build a safety connection because the connection doesn't send any bytes.");
                return false;
            }
            CableMessageHead cableMessageHead = CableMessageHead.fromBytes(bytes);
            if(cableMessageHead.getType() == CableMessageType.HELLO){
                byte[] helloBytes = SocketUtil.readSync(p1, cableMessageHead.getLength());
                if(helloBytes == null){
                    System.out.println("Fail to build a safety connection with p1, connection closed.");
                    return false;
                }
                String helloRlyMsg = new String(helloBytes,StandardCharsets.UTF_8);
                System.out.println("Built a safety connection with p1 successfully.");
                System.out.println(helloRlyMsg);
                return true;
            }else{
                System.out.println("Fail to build a safety connection, reject by p1.");
                return false;
            }
        } catch (IOException | CableMessageException e) {
            System.out.println("Fail to build a safety connection due to the exception: "+e.getMessage()+".");
            return false;
        }
    }

    /**
     * This is a function to listen CableMessage from p1
     * @param listener You should offer a listener then you will catch the callback events of the listening
     */
    private void listen(CableMessageListener listener){
        System.out.println("Listening CableMessage from p1");
        try {
            while (!p1.isClosed()){
                byte[] bytes = SocketUtil.readSync(p1, CableMessageHead.SIZE);
                if(bytes == null) break;
//                // If you want to see what the data p2 received is, release followed component.
//                System.out.println("---------------------");
//                System.out.println(new String(bytes, StandardCharsets.UTF_8));
//                System.out.println("----------------------");
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
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CableMessageException e) {
            System.out.println("Receive an unknown message:"+ e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

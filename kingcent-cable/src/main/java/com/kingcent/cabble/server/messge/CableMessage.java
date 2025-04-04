package com.kingcent.cabble.server.messge;


import java.util.UUID;

public class CableMessage {
    private final CableMessageHead head;
    private final byte[] body;

    public CableMessage(CableMessageHead head, byte[] body) {
        this.head = head;
        this.body = body;
    }

    public static CableMessage pingPong() {
        return new CableMessage(
                new CableMessageHead(UUID.randomUUID(),CableMessageType.PING_PONG,0,"0",0,"0",0),
                new byte[0]
        );
    }

    public byte[] getBytes(){
        byte[] headBytes = head.getBytes();
        byte[] res = new byte[headBytes.length+body.length];
        System.arraycopy(headBytes,0,res,0,headBytes.length);
        System.arraycopy(body,0,res,headBytes.length,body.length);
        return res;
    }

    public static CableMessage hello(HelloInfo helloInfo){
        byte[] body = helloInfo.getBytes();
        return new CableMessage(CableMessageHead.hello(body.length), body);
    }

    public static CableMessage outerClose(CableMessageHead head){
        return new CableMessage(CableMessageHead.outerClose(head),new byte[0]);
    }
}

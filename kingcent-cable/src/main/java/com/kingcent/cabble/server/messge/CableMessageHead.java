package com.kingcent.cabble.server.messge;

import com.kingcent.cabble.server.exception.CableMessageException;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CableMessageHead {

    private static final String TAG = "KingCable/1.0";
    public static final int SIZE = 216;

    private UUID uuid;
    private CableMessageType type;
    private Integer length;
    private String clientHost;
    private Integer clientPort;
    private String serverHost;
    private Integer serverPort;

    public CableMessageHead(UUID uuid, CableMessageType type, Integer length, String clientHost, Integer clientPort, String serverHost, Integer serverPort) {
        this.uuid = uuid;
        this.type = type;
        this.length = length;
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }


    public static CableMessageHead hello(int length){
        return new CableMessageHead(UUID.randomUUID(),CableMessageType.HELLO,length,"0",0,"0",0);
    }
    public static CableMessageHead outerClose(CableMessageHead head){
        return new CableMessageHead(UUID.randomUUID(),CableMessageType.OUTER_CLOSE,0,head.getClientHost(),head.getClientPort(),head.getServerHost(),head.getServerPort());
    }

    public static CableMessageHead reply(CableMessageHead head, int length){
        return new CableMessageHead(head.uuid,head.type,length,head.clientHost,head.clientPort,head.serverHost,head.serverPort);
    }

    public static CableMessageHead forward(int length, String clientHost, int clientPort, String serverHost, int serverPort){
        return  new CableMessageHead(UUID.randomUUID(),CableMessageType.FORWARD,length,clientHost,clientPort,serverHost,serverPort);
    }

    public byte[] getBytes(){
        byte[] payload =  (TAG+"\n"+uuid+"\n"+type.value+"\n"+length+"\n"+clientHost+"\n"+clientPort+"\n"+serverHost+"\n"+serverPort+"\n").getBytes(StandardCharsets.UTF_8);
        byte[] res = new byte[SIZE];
        System.arraycopy(payload,0,res,0,payload.length);
        return res;
    }

    private static CableMessageType parseMessageType(String val) throws CableMessageException {
        return switch (val){
            case "0" -> CableMessageType.HELLO;
            case "1" -> CableMessageType.FORWARD;
            case "2" -> CableMessageType.OUTER_CLOSE;
            default -> throw new CableMessageException("Unknown message type "+val);
        };
    }

    public static CableMessageHead fromBytes(byte[] data) throws CableMessageException {
        if(data.length != SIZE) throw CableMessageException.ERROR_CABLE_MESSAGE_HEAD;
        String head = new String(data,StandardCharsets.UTF_8);
        if(!head.startsWith(TAG)) throw CableMessageException.ERROR_CABLE_MESSAGE_HEAD;
        String[] split = head.split("\n");
        if(split.length != 9) throw CableMessageException.ERROR_CABLE_MESSAGE_HEAD;
        try{
            return new CableMessageHead(
                    UUID.fromString(split[1]),
                    parseMessageType(split[2]),
                    Integer.parseInt(split[3]),
                    split[4],
                    Integer.parseInt(split[5]),
                    split[6],
                    Integer.parseInt(split[7])

            );
        }catch (CableMessageException e){
            throw e;
        }catch (Exception e){
            throw CableMessageException.ERROR_CABLE_MESSAGE_HEAD;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public CableMessageType getType() {
        return type;
    }

    public void setType(CableMessageType type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }
}

package com.kingcent.net;



import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HandMessageHead {

    public enum Type {
        TYPE_HELLO("0"),
        TYPE_REQUEST("1"),
        TYPE_RESPONSE("2"),
        TYPE_GOODBYE("3"),
        SERVER_CLOSE("4"),
        CLIENT_CLOSE("5");

        public final String value;
        Type(String value){
            this.value = value;
        }
    }

    public static final int SIZE = 1024;

    // 消息ID
    public UUID uuid;
    // 数据包长度
    public Integer length;
    // 转发到的主机
    public String host;
    // 转发到的端口
    public Integer port;
    // 消息类型
    public Type type;
    // 客户名称（IP:PORT）
    public String clientName;

    private static Type parseType(String value) throws Exception {
        return switch (value) {
            case "0" -> Type.TYPE_HELLO;
            case "1" -> Type.TYPE_REQUEST;
            case "2" -> Type.TYPE_RESPONSE;
            case "3" -> Type.TYPE_GOODBYE;
            case "4" -> Type.SERVER_CLOSE;
            case "5" -> Type.CLIENT_CLOSE;
            default -> throw new Exception();
        };
    }

    public static HandMessageHead parseHead(byte[] data){
        String head = new String(data);
        if(!head.startsWith("KING-NET/1.0")) return null;
        String[] headInfo = head.split("\n");
        try{
            if(headInfo.length == 7 || headInfo.length == 8){
                return new HandMessageHead(
                        UUID.fromString(headInfo[1]),
                        Integer.parseInt(headInfo[2]),
                        headInfo[3],
                        Integer.parseInt(headInfo[4]),
                        parseType(headInfo[5]),
                        headInfo[6]
                );
            }
        }catch (Exception ignored){}
        return null;
    }

    /**
     * 将消息头转为字节流
     * @return 字节流（长度:SIZE）
     */
    public byte[] getBytes(){
        byte[] buffer = new byte[SIZE];
        byte[] s =("KING-NET/1.0\n"+this.uuid+"\n"+this.length+"\n"+this.host +"\n"+this.port+"\n"+this.type.value+"\n"+this.clientName +"\n").getBytes(StandardCharsets.UTF_8);
        System.arraycopy(s, 0, buffer, 0, s.length);
        return buffer;
    }
}

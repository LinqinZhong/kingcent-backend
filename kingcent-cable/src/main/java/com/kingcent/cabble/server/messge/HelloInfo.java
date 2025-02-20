package com.kingcent.cabble.server.messge;



import java.nio.charset.StandardCharsets;

public class HelloInfo {
    private String serviceName;
    private String secret;

    public HelloInfo(String serviceName, String secret) {
        this.serviceName = serviceName;
        this.secret = secret;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public byte[] getBytes() {
        return (serviceName+"\n"+secret).getBytes(StandardCharsets.UTF_8);
    }

    public static HelloInfo fromBytes(byte[] bytes){
        try{
            String info = new String(bytes,StandardCharsets.UTF_8);
            String[] split = info.split("\n");
            if(split.length == 2){
                return new HelloInfo(split[0],split[1]);
            }
        }catch (Exception ignored){}
        return null;
    }
}

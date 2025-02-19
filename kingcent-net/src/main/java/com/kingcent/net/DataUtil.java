package com.kingcent.net;

public class DataUtil {
    public static byte[] concatBytes(byte[] a, byte[] b){
        byte[] res = new byte[a.length+b.length];
        System.arraycopy(a,0,res,0,a.length);
        System.arraycopy(b,0,res,a.length,b.length);
        return  res;
    }
}

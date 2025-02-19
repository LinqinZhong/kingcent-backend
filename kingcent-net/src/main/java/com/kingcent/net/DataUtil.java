package com.kingcent.net;

import java.nio.charset.StandardCharsets;

public class DataUtil {
    public static byte[] concatBytes(byte[] a, byte[] b){
        byte[] res = new byte[a.length+b.length];
        System.arraycopy(a,0,res,0,a.length);
        System.arraycopy(b,0,res,a.length,b.length);
        return  res;
    }

    public static void displayBytes(byte[] b){
        int i = 1;
        for (byte b1 : b) {
            System.out.printf("%d ",b1);
            if((i++)%20 == 0) System.out.println();
        }
    }
}

package com.kingcent.campus.auth.utils;


import cn.hutool.crypto.digest.MD5;

public class SecretEncryptUtil {

    private static int avg(int[] num){
        int avg = 0;
        for (int i : num) {
            avg += i;
        }
        avg /= num.length;
        return avg;
    }

    private static int[] longToArray(long a){
        int[] arr = new int[String.valueOf(a).length()];
        int i = 0;
        while (a > 0) {
            arr[i] = (int) (a % 10);
            a /= 10;
            i++;
        }
        return arr;
    }
    public static String encrypt(long uid, String secret, String code) {
        String key = MD5.create().digestHex(code.getBytes()).substring(8, 16);

        int[] suid = longToArray(uid);
        char[] sSecret = secret.toCharArray();
        int k = suid[0];
        int j = 0;
        int avg = avg(suid);
        for(int i = 0; i < sSecret.length; i++){
            System.out.println("哈哈"+avg+","+suid[j]);
            sSecret[k] += suid[j] + ((j&1) == 0 ? avg : -avg);
            k ++;
            j ++;
            if(k >= sSecret.length) k = 0;
            if(j >= suid.length) j = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : sSecret) {
            sb.append(c);
        }
        String res = DESUtil.encrypt(key, sb.toString());
        System.out.println(sb+","+code+","+secret);
        return res;

    }

    public static String decrypt(long uid, String secret, String code) {
        String key = MD5.create().digestHex(code.getBytes()).substring(8, 16);
        secret = DESUtil.decrypt(key, secret);
        int[] suid = longToArray(uid);
        assert secret != null;
        char[] sSecret = secret.toCharArray();
        int k = suid[0];
        int j = 0;
        int avg = avg(suid);
        for(int i = 0; i < sSecret.length; i++){
            sSecret[k] -= suid[j] + ((j&1) == 0 ? avg : -avg);
            j ++;
            k ++;
            if(k >= sSecret.length) k = 0;
            if(j >= suid.length) j = 0;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : sSecret) {
            sb.append(c);
        }
        return sb.toString();
    }

}
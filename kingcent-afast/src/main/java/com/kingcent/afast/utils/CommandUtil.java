package com.kingcent.afast.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * @author rainkyzhong
 * @date 2024/10/13 1:09
 */
public class CommandUtil {
    public interface CommandListener{
        void onMessage(String msg);
        void onError(String err);
    }
    public static void exec(String path, String[] commands, CommandListener commandListener) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(path));
        Process p1= pb.start();
        BufferedReader reader = p1.inputReader();
        String msg;
        String error = null;
        BufferedReader reader1 = p1.errorReader();
        while((msg = reader.readLine()) != null || (error = reader1.readLine()) != null){
            if(msg != null) commandListener.onMessage(msg);
            if(error != null) commandListener.onError(error);
        }
    }
}

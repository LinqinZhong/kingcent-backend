package com.kingcent.net;

import com.kingcent.net.server.P1;

import java.io.IOException;


//@MapperScan("com.kingcent.net.mapper")
//@SpringBootApplication
public class NetApplication {

    public static void main(String[] args) throws IOException {
        new P1().start();
    }

}

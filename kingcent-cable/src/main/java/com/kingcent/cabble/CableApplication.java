package com.kingcent.cabble;

import com.kingcent.cabble.server.CableServer;
import com.kingcent.cabble.server.P1;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CableApplication {

	public static void main(String[] args) {
		new P1(8889).start();
		new CableServer(10000).start();
		while (true);
	}

}

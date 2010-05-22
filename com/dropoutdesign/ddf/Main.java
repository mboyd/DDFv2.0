package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.client.*;
import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.test.*;

import java.io.IOException;
import javax.imageio.*;

public class Main {
	
	public static void main(String args[]) throws IOException {
		
		if (args.length > 0 && args[0].equals("-server")) {
		
			try {
				DanceFloor floor = DanceFloor.connectFloor("virtual:");
				DDFServer server = new DDFServer(floor);
				server.start();
			
			} catch (Exception e) {
				System.out.println("Unable to start server: " + e);
			}
		
		} else {
			ClientGUI myGUI = new ClientGUI("localhost");
		}
	}
}

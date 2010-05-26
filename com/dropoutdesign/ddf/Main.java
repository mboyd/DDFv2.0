package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.client.*;
import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.test.*;

import java.io.IOException;
import javax.imageio.*;

/**
 * Main class for interactive use.
 */
public class Main {
	
	/**
	 * Process command line arguments, and either start a DDF Server or Client.
	 */
	public static void main(String args[]) throws IOException {
		
		if (args.length > 0 && args[0].equals("-server")) {
			String floorPath = "local:config.xml";
			
			if (args.length >= 2) {
				floorPath = args[1];
			}
				
			try {
			
				DanceFloor floor = DanceFloor.connectFloor(floorPath);
				DDFServer server = new DDFServer(floor);
				server.start();
		
			} catch (Exception e) {
				System.out.println("Unable to start server: " + e);
				e.printStackTrace();
			}
		
		} else {
			ClientGUI myGUI = new ClientGUI("dancefloor.mit.edu");
		}
	}
}

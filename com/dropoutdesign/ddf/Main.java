package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.client.*;
import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.test.*;

import java.io.IOException;
import javax.imageio.*;

public class Main {
	
	public static void main(String args[]) throws IOException {
		
		/*VirtualFloor floor = new VirtualFloor();
		SeqTest test = new SeqTest();
		test.test(floor);
		//test.loadDDF(floor, "1ediag.ddf");
		*/
		
		if (args.length > 0 && args[0].equals("-server")) {
		
			DanceFloorConfig dfc = DanceFloorConfig.readAll("config.xml");
			DanceFloor floor = new VirtualFloor();
			DDFServer server = new DDFServer(floor);
			server.start();
		
		} else {
			ClientGUI myGUI = new ClientGUI("localhost");	
		}
	}
}

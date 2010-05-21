package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.client.ClientGUI;
import com.dropoutdesign.ddf.config.*;

import java.io.IOException;

public class Main {
	
	public static void main(String args[]) throws IOException {
		if (args.length > 0 && args[0].equals("-server")) {
		
			DanceFloorConfig dfc = DanceFloorConfig.readAll("config.xml");
			DanceFloor floor = new DanceFloor(dfc);
			WriteThread writeThread = new WriteThread(floor);
			writeThread.start();
		
		} else {
		
			ClientGUI myGUI = new ClientGUI();	
		}
	}
}

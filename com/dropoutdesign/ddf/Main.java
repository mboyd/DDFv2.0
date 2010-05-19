package com.dropoutdesign.ddf;

import client.dropoutdesign.ddf.ClientGUI;

import com.dropoutdesign.ddf.config.*;
import java.io.IOException;

public class Main {
	
	public static void main(String args[]) throws IOException {
		//ClientGUI myGUI = new ClientGUI();
	
		DanceFloorConfig dfc = DanceFloorConfig.readAll("config.xml");
		DanceFloor floor = new DanceFloor(dfc);
		WriteThread writeThread = new WriteThread(floor);
		writeThread.start();
		
		/* try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myGUI.intialConnect(); */
	}
}

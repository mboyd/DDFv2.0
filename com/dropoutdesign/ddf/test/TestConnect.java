package com.dropoutdesign.ddf.test;

import com.dropoutdesign.ddf.*;

import java.net.*;
import java.io.*;

public class TestConnect {

	public boolean test(VirtualFloor myFloor) {
			loadDDF(myFloor, "1ediag.ddf");
			return true;
		
			/*try {
				RemoteFloor myFloor = new RemoteFloor("dancefloor.mit.edu");
				System.out.println("Connected");
				
				try{ Thread.sleep(2000); } catch (Exception e) {}
				
				loadDDF(myFloor, "inter4.ddf");
			
			} catch (UnknownHostException e) {
				System.out.println("Unable to reach dancefloor.mit.edu");
				System.exit(1);
			
			} catch (IOException e) {
				System.out.println("Unable to connect to server on dancefloor.mit.edu");
				System.exit(1);
			}*/
		
	}

	public int loadDDF(VirtualFloor myFloor, String fileName) {
		int numframes = 0, k;
		int ROWS = myFloor.getWidth();
		int COLS = myFloor.getHeight();
		System.out.println("Rows: " + ROWS);
		System.out.println("Columns: " + COLS);
		//This is really lame.  I'll fix it later.
		byte data[][] = new byte[1000][ROWS*COLS*3];
		
		try {
			FileInputStream ddfFile = new FileInputStream(fileName);
			int status = 1;
			while(status != -1 && numframes < 1000) {
				// System.out.println(numframes);
				status = ddfFile.read(data[numframes], 0, ROWS*COLS*3);
				numframes++;
			}
			numframes--;
			
			System.out.println(numframes + " frames loaded");
			ddfFile.close();
		
		} catch (FileNotFoundException e) {
			System.out.println("Unable to locate pattern: " + fileName);
			System.exit(1);

		} catch (IOException e) {
			System.out.println("Unable to load pattern: " + fileName);
			System.exit(1);
		}

		myFloor.drawFrame(data[0]);
	
		return 0;
	}
}
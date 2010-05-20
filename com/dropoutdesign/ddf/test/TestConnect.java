package com.dropoutdesign.ddf.test;

import com.dropoutdesign.ddf.client.FloorWriter;

import java.net.*;
import java.io.*;

public class TestConnect {

	public static void main(String[] args) {
			try {
				FloorWriter myFloor = new FloorWriter("dancefloor.mit.edu");
				System.out.println("Connected");
				
				try{ Thread.sleep(2000); } catch (Exception e) {}
				
				loadDDF(myFloor, "inter4.ddf");
			
			} catch (UnknownHostException e) {
				System.out.println("Unable to reach dancefloor.mit.edu");
				System.exit(1);
			
			} catch (IOException e) {
				System.out.println("Unable to connect to server on dancefloor.mit.edu");
				System.exit(1);
			}
		
	}

	public static int loadDDF(FloorWriter myFloor, String fileName) {
	  int numframes = 0, k;
	  int ROWS = myFloor.getFloorWidth();
	  int COLS = myFloor.getFloorHeight();
	  System.out.println("Rows: " + myFloor.getFloorWidth());
	  System.out.println("Columns: " + myFloor.getFloorHeight());
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

		  //System.out.println(numframes + " frames loaded");

		  
		  ddfFile.close();
	} catch (FileNotFoundException e) {
		System.out.println("Unable to locate pattern: " + fileName);
		System.exit(1);

	} catch (IOException e) {
		System.out.println("Unable to load pattern: " + fileName);
		System.exit(1);
	}

		try {
			
			//myFloor.waitAndSend(data[0]);
			if (numframes==1) {
				myFloor.waitAndSend(data[0]);
			} else {
				while(true) {
					for(k=0;k<(numframes);k++) {
						myFloor.waitAndSend(data[k]);
						//System.out.println(k + "frames sent");
						//usleep(100000);
						//usleep(200000);
					}
//				for(k=(numframes-5);k>=0;k--) {
//					write_dancefloor(fl,data[k]);
						//usleep(100000);
						//usleep(200000);
//				}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return 0;
	}

}
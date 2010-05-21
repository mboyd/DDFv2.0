package com.dropoutdesign.ddf.client;

import com.dropoutdesign.ddf.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadThread extends Thread {
	
	private String myFileName;
	private PublishThread myPublisher;
	private static RemoteFloor myFloor;
	private static byte data[][];
	private static int numframes;
	
	public LoadThread(String str, String fileName, PublishThread myPub, RemoteFloor myF) {
		super(str);
		myFileName = fileName;
		myPublisher = myPub;
		myFloor = myF;
	}
	
	public void run() {
		loadDDF("patterns/" + myFileName + ".ddf");
		myPublisher.setSwitchPattern(true);
	}
	
	public void setFileName(String s) {
		myFileName = s;
	}
	
	public static void loadDDF(String fileName) {
		numframes = 0;

		int ROWS = myFloor.getWidth();
		int COLS = myFloor.getHeight();

		data = new byte[1000][ROWS*COLS*3];

		try {
			FileInputStream ddfFile = new FileInputStream(fileName);
			int status = 1;
			while(status != -1 && numframes < 1000) {
				// System.out.println(numframes);
				status = ddfFile.read(data[numframes],0,ROWS*COLS*3);
				numframes++;
			}

			ddfFile.close();
			System.out.println("[Load] Read " + numframes + " frames.");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public static byte[][] getData() {
		return (byte[][])data.clone();
	}
	
	public static int getNumFrames() {
		return numframes;
	}
}

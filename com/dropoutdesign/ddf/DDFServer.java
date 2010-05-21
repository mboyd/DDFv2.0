package com.dropoutdesign.ddf;

import java.util.*;
import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.module.*;
import java.io.*;
import java.net.*;

public class DDFServer extends Thread {

	public static final int SERVER_PORT = 6002;

	private DanceFloor floor;
	
	private int counter = 0; 
	
	private boolean DEBUG = true;

	public DDFServer(DanceFloor floor) {
		super("Disco Dance Floor Writer");
		this.floor = floor;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		try {
			 serverSocket = new ServerSocket(SERVER_PORT);
		}
		catch (IOException e) {
			 System.err.println("Could not start server write listener on port " + SERVER_PORT);
			 System.err.println(e);
			 System.exit(1);
		}
		
		Socket clientSocket = null;
		int iconnect = 0;
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				iconnect++;
				System.out.println("Disco!");
				System.out.println("Connection Number: " + iconnect);
			} catch (IOException e) {
				System.err.println("Error accepting connection on port " + SERVER_PORT);
				System.err.println(e);
				continue;
			}
			
			try {
				DataInputStream input = new DataInputStream(clientSocket.getInputStream());
				OutputStream output = clientSocket.getOutputStream();
				byte firstFourBytes[] = new byte[4];
				input.readFully(firstFourBytes);

				if (firstFourBytes[0] != (byte)'D' || firstFourBytes[1] != (byte)'D' 
													|| firstFourBytes[2] != (byte)'F') {
					throw new DDFServerException("Writer client didn't send 'DDF'");			 
				}

				output.write((byte)'D');
				output.write((byte)'D');
				output.write((byte)'F');
				output.write((byte)0x00);
			
				int floorWidth = floor.getWidth();
				output.write((byte)(floorWidth>>8));
				output.write((byte)(floorWidth));
			
				int floorHeight = floor.getHeight();
				output.write((byte)(floorHeight>>8));
				output.write((byte)(floorHeight));
				output.flush();
				
				byte currentFrame[] = new byte[floorWidth*floorHeight*3];
			
				int frameMinMillis = (int)(1000.0/floor.getMaxFPS());
				long lastFrameTime = System.currentTimeMillis();
			
				while (!clientSocket.isInputShutdown()) {
				
					debug("Waiting for frame...");
					if (!clientSocket.isInputShutdown()) {
					 	input.readFully(currentFrame);
					 	debug("Received frame");
					}
					
					floor.drawFrame(currentFrame);
			 
					int msToWait = (int)((lastFrameTime + frameMinMillis) 
										- System.currentTimeMillis());
					if (msToWait > 0) {
						//debug("Finished " + msToWait + "ms early.");
						try { Thread.sleep(msToWait); } catch (InterruptedException e) {}
					
					} else if (msToWait < 0) {
						debug("Finished " + msToWait + "ms late.");
					}
					
					lastFrameTime = System.currentTimeMillis();
				}
			 
			} catch (IOException e) {
				System.err.println("Closing writer connection.");
				System.err.println(e);
				try {
					clientSocket.shutdownInput();
					clientSocket.shutdownOutput();
				 	clientSocket.close();
				} catch (IOException f) {
				 // ignore
				}
				continue;
			
			} catch (DDFServerException e) {
				System.err.println("Server Error: closing writer connection.");
				System.err.println(e);
				try {
					clientSocket.shutdownInput();
					clientSocket.shutdownOutput();
				 	clientSocket.close();
				} catch (IOException f) {
				 // ignore
				}
				continue;
			
			} catch (Exception e) {
				System.err.println("Server Error: closing writer connection.");
				System.err.println(e);
				try {
					clientSocket.shutdownInput();
					clientSocket.shutdownOutput();
				 	clientSocket.close();
				} catch (IOException f) {
				 // ignore
				}
				continue;
			}
		}
	}
	
	private void debug(String str) {
		if (DEBUG)
			System.err.println(str);
	}
}

package com.dropoutdesign.ddf;

import java.util.*;
import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.module.*;
import java.io.*;
import java.net.*;

public class WriteThread extends Thread {

	public static final int WRITER_PORT = 6002;

	private DanceFloor floor;
	
	private int counter = 0; 
	
	private boolean DEBUG = false;

	public WriteThread(DanceFloor floor) {
		super("Disco Dance Floor Writer");
		this.floor = floor;
	}
	
	public void run() {
		ServerSocket serverSocket = null;
		try {
			 serverSocket = new ServerSocket(WRITER_PORT);
		}
		catch (IOException e) {
			 System.err.println("Could not start server write listener on port " + WRITER_PORT);
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
				System.err.println("Error accepting connection on port " + WRITER_PORT);
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

				byte frameBytes[] = new byte[floorWidth*floorHeight*3];
				byte writeCommandBytes[] = new byte[97];
				byte responseBytes[] = new byte[1];
		
				List moduleList = floor.getModules();
				Module modules[] = new Module[moduleList.size()];
				for (int i = 0; i < modules.length; i++) {
					 modules[i] = (Module) moduleList.get(i);
				}
				ModuleConnection connections[] = new ModuleConnection[modules.length];
			
				int frameMinMillis = (int)(1000.0/floor.getMaxFPS());
				long lastFrameTime = System.currentTimeMillis();
			
				while (!clientSocket.isInputShutdown()) {
					for(int i = 0; i < modules.length; i++) {
						connections[i] = modules[i].getConnection();
						debug("Connection " + i + ": " + connections[i]);
					}
				 
					writeCommandBytes[0] = 0x10;
				 
					for(int i = 0; i < modules.length; i++) {
						if (connections[i] != null) {
							assembleWriteCommand(writeCommandBytes, frameBytes, modules[i]);
							try {
								connections[i].sendCommand(writeCommandBytes);
							} catch (ModuleIOException e) {
								System.out.println("Error with module " + i + ". Closing connection.");
								System.err.println(e);
								modules[i].errorOccurred();
								connections[i] = null;
							}
						}
					}
				
					output.write((byte)0x00);
					output.flush();
					debug("Sent byte");
				
					debug("Waiting for frame...");
					if (!clientSocket.isInputShutdown()) {
					 	input.readFully(frameBytes);
					 	debug("Received frame");
					}
			 	
			 	
					long timeoutTime = lastFrameTime + (long)frameMinMillis;
					boolean stillWaiting = true;
					while (stillWaiting) {
						if (System.currentTimeMillis() - timeoutTime > 0) {
							System.out.print("Timeout reading response from modules.");
						 	break;
						}
						stillWaiting = false;
						for (int i = 0; i < connections.length; i++) {
							if (connections[i] != null) {
								try {
							 		if (! connections[i].tryReadResponse(responseBytes)) {
										stillWaiting = true;
										break;
							 		} else {
										// response is in responseBytes	
										/* if (responseBytes[0] != 0) {
											String connectionName = connections[i].getName();
											System.out.println((counter++) + " " 
												+ connections[i].getName()+" gave response: "
							  					+ Integer.toString(responseBytes[0], 16));
										} */
							 		}
								} catch (ModuleIOException e) {
							 		modules[i].errorOccurred();
							 		connections[i] = null;
								}
							}
						}
						if (stillWaiting) {
							try { Thread.sleep(1); } catch (InterruptedException e) {}
						}
					}
			 
					int millisToWait = (int)(lastFrameTime 
										+ ((long)frameMinMillis) - System.currentTimeMillis());
					if (millisToWait > 0) {
						debug("Finished early, sleeping for " + millisToWait + "ms");
						try { Thread.sleep(millisToWait); } catch (InterruptedException e) {}
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
			 }
		}
	}
	
		 // dest must be 97 bytes, frameBytes must be width*height*3
		 // first byte of dest is untouched
	private void assembleWriteCommand(byte dest[], byte frameBytes[], Module m) {
		List coords = m.getPixelCoords();
		int curNib = 2;

		for (int q = 0; q < 4; q++) {
			 for (int c = 0; c < 3; c++) {
				for (int led = q*16; led < (q+1)*16; led++) {
					XYCoords xy = (XYCoords) coords.get(led);
				 	int x = xy.x;
					int y = xy.y;
					int whichByte = (y*floor.getWidth() + x)*3 + c;
					int nib = ((frameBytes[whichByte] >> 4) & 0xF) ^ 0xF; // flip because 0xF is off
					int curByte = curNib/2;
					
					if ((curNib % 2) == 0) {
						dest[curByte] = (byte)(nib);
					} else {
						dest[curByte] |= (byte)(nib << 4);
					}
				curNib++;
				}
			}
		}
	}
	
	private void debug(String str) {
		if (DEBUG)
			System.err.println(str);
	}
}

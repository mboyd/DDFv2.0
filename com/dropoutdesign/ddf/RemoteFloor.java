package com.dropoutdesign.ddf;

import java.net.*;
import java.io.*;

public class RemoteFloor extends DanceFloor {

	public static final int DEFAULT_PORT = 6002;

	private String address;
	private int port;

	private Socket socket;
	
	private OutputStream output;
	private DataInputStream input;
	
	private int width;
	private int height;
	private int framerate;

	public RemoteFloor(String serverAddress) throws UnknownHostException, IOException {
		address = serverAddress;
		port = DEFAULT_PORT;
		
		int i = serverAddress.indexOf(":");
		if (i != -1) {
			try {
				port = Integer.parseInt(serverAddress.substring(i+1, serverAddress.length()));
				address = serverAddress.substring(0, i);
			} catch (NumberFormatException e) {
				// Ignore
			}
		}
		
		socket = new Socket();
		width = -1;
		height = -1;
		framerate = -1;
	}
	
	public RemoteFloor(String srvAddress, int srvPort) throws UnknownHostException, IOException {
		address = srvAddress;
		port = srvPort;
		socket = new Socket();
		
		width = -1;
		height = -1;
		framerate = -1;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getFramerate() {
		return framerate;
	}
	
	public void drawFrame(byte frame[]) throws IOException {
		if (frame.length != width*height*3) {
			throw new IOException("Wrong number of frame pixels.");
		}
		output.write(frame);
	}
	
	public boolean isConnected() {
		return !socket.isClosed();
	}
	
	public void connect() throws UnknownHostException, IOException {
		socket.connect(new InetSocketAddress(address, port), 2000);
		output = socket.getOutputStream();
		input = new DataInputStream(socket.getInputStream());
		
		output.write((byte)'D');
		output.write((byte)'D');
		output.write((byte)'F');
		output.write((byte)0x00);
		
		byte response[] = new byte[10];
		input.readFully(response);
		if (response[0] != (byte)'D' || response[1] != (byte)'D' || response[2] != (byte)'F') {
			throw new IOException("Didn't get DDF response");
		}
		
		width = ((response[4]&0xFF) << 16) | (response[5]&0xFF);
		height = ((response[6]&0xFF) << 16) | (response[7]&0xFF);
		framerate = ((response[8]&0xFF) << 16) | (response[9]&0xFF);
	}
	
	public void disconnect() {
		try {
			socket.shutdownOutput();
			socket.shutdownInput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = -1;
		height = -1;
	}
}

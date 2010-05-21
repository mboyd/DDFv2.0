package com.dropoutdesign.ddf;

import java.net.*;
import java.io.*;

public class RemoteFloor extends DanceFloor {

	public static final int DEFAULT_PORT = 6002;

	private Socket socket;
	
	private OutputStream output;
	private DataInputStream input;
	
	private int width;
	private int height;

	public RemoteFloor(String serverAddress, int port) throws UnknownHostException, IOException {
		connect(serverAddress, port);
	}
	
	public RemoteFloor(String serverAddress) throws UnknownHostException, IOException {
		this(serverAddress, DEFAULT_PORT);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getMaxFPS() {
		return 15;
	}
	
	public void drawFrame(byte frame[]) throws IOException {
		if (frame.length != width*height*3) {
			throw new IOException("Wrong number of frame pixels");
		}
		output.write(frame);
	}
	
	public boolean isConnected() {
		return !socket.isClosed();
	}
	
	public void connect(String serverAddress) throws UnknownHostException, IOException {
		connect(serverAddress, DEFAULT_PORT);
	}
	
	public void connect(String serverAddress, int port) throws UnknownHostException, IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(serverAddress, port), 2000);
		output = socket.getOutputStream();
		input = new DataInputStream(socket.getInputStream());
		
		output.write((byte)'D');
		output.write((byte)'D');
		output.write((byte)'F');
		output.write((byte)0x00);
		
		byte response[] = new byte[8];
		input.readFully(response);
		if (response[0] != (byte)'D' || response[1] != (byte)'D' || response[2] != (byte)'F') {
			throw new IOException("Didn't get DDF response");
		}
		
		width = ((response[4]&0xFF) << 16) | (response[5]&0xFF);
		height = ((response[6]&0xFF) << 16) | (response[7]&0xFF);
	}
	
	public void disconnect() {
		try {
			socket.shutdownOutput();
			socket.shutdownInput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

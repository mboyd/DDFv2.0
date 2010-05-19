package com.dropoutdesign.ddf.client;

import java.io.*;
import java.net.*;

public class FloorWriter {
	
	public static final int DEFAULT_PORT = 6001;
	
	private Socket socket;
	private OutputStream output;
	private DataInputStream input;
	private int width;
	private int height;

	public FloorWriter(String serverAddress, int port) throws UnknownHostException, IOException {
		InetAddress addr = InetAddress.getByName(serverAddress);
		socket = new Socket(addr, port);
		
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

	public FloorWriter(String serverAddress) throws UnknownHostException, IOException {
		this(serverAddress, DEFAULT_PORT);
	}
	
	public void waitNextFrame() throws IOException {
		//System.err.println("FloorWriter waiting");
		input.read();
		//System.err.println("FloorWriter got byte");
	}
	
	public void sendFrame(byte pixels[]) throws IOException {
		if (pixels.length != width*height*3) {
			throw new IllegalArgumentException("Wrong number of frame pixels");
		}
		output.write(pixels);
	}

	public void waitAndSend(byte pixels[]) throws IOException {
		waitNextFrame();
		sendFrame(pixels);
	}
	
	public int getFloorWidth() {
		return width;
	}
	
	public int getFloorHeight() {
		return	height;
	}
}

package com.dropoutdesign.ddf.client;

import java.io.*;
import java.net.*;

public class FloorWriter {

	public static final int DEFAULT_PORT = 6002;

	private Socket socket;
	
	private OutputStream output;
	private DataInputStream input;
	
	private int width;
	private int height;

	public FloorWriter(String serverAddress, int port) throws UnknownHostException, IOException {
		connect(serverAddress, port);
	}
	
	public void connect(String serverAddress) throws UnknownHostException, IOException {
		connect(serverAddress, DEFAULT_PORT);
	}
	
	public void connect(String serverAddress, int port) throws UnknownHostException, IOException {
		InetAddress addr;
		if (serverAddress.equals("localhost")) {
			addr = InetAddress.getByName(null);
		} else {
			addr = InetAddress.getByName(serverAddress);
		}
		
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
	
	public boolean isClosed() {
		return socket.isClosed();
	}

	public void waitNextFrame() throws IOException {
		input.read();
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

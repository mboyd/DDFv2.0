package com.dropoutdesign.ddf;

import java.net.*;
import java.io.*;

/**
 * Allows inspection and control of a Dance Floor published over the network by a DDF Server.
 */
public class RemoteFloor extends DanceFloor {

	/**
	 * Default port on which to connect the DDF Server.
	 */
	public static final int DEFAULT_PORT = 6002;

	private String address;
	private int port;

	private Socket socket;
	
	private OutputStream output;
	private DataInputStream input;
	
	private int width;
	private int height;
	private int framerate;

	/**
	 * Create a new Remote Floor, corresponding to the supplied network address.
	 * The address can be specified in host:port format, or else the default port will be used.
	 */
	public RemoteFloor(String serverAddress) {
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
	
	/**
	 * Create a new Remote Floor with the specified address and port.
	 */
	public RemoteFloor(String srvAddress, int srvPort) {
		address = srvAddress;
		port = srvPort;
		socket = new Socket();
		
		width = -1;
		height = -1;
		framerate = -1;
	}
	
	/**
	 * Return the width of this floor, or -1 if the floor has not yet been connected.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Return the height of this floor, or -1 if the floor has not yet been connected.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Return the native framerate of this floor, or -1 if the floor has not yet been connected.
	 */
	public int getFramerate() {
		return framerate;
	}
	
	/**
	 * Render a frame onto this floor.
	 * @param frame the frame to draw.
	 * @throws IOException the frame could not be drawn.
	 * @see com.dropoutdesign.ddf.DanceFloor#drawFrame
	 */
	public void drawFrame(byte frame[]) throws IOException {
		if (frame.length != width*height*3) {
			throw new IOException("Wrong number of frame pixels.");
		}
		output.write(frame);
	}
	
	/**
	 * Return the current connection status of this floor.
	 */
	public boolean isConnected() {
		return !socket.isClosed();
	}
	
	/**
	 * Connect to this floor.
	 * @throws UnknownHostException the specified address is unreachable or invalid.
	 * @throws IOException the connection could not be established.
	 */
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
	
	/**
	 * Disconnect from this floor.
	 */
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

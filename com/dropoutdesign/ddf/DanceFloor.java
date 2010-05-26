package com.dropoutdesign.ddf;

import java.net.InetAddress;

/**
 * Base class for all dance floor implementations.
 * Supports basic connnection management, capability inspection, and pixel-based rendering.
 */
public abstract class DanceFloor {
	
	/**
	 * Connect to this floor.
	 * @throws Exception the connection could not be established.
	 */
	public abstract void connect() throws Exception;
	
	/**
	 * Disconnect from this floor.
	 */
	public abstract void disconnect();
	
	/**
	 * Return the current connection status.
	 */
	public abstract boolean isConnected();

	/**
	 * Get the width of this floor, in pixels.
	 */
	public abstract int getWidth();
	
	/**
	 * Get the height of this floor, in pixels.
	 */
	public abstract int getHeight();
	
	/**
	 * Get the native framerate of this floor, in frames per second.
	 */
	public abstract int getFramerate();
	
	/**
	 * Draw a frame onto the floor.
	 * Frames are stored as one dimensional arrays
	 * of bytes, with 1 byte / component interleaved in RGB order.
	 * @param frame the frame to render.
	 * @throws Exception the frame could not be drawn.
	 */
	public abstract void drawFrame(byte frame[]) throws Exception;
	
	/**
	 * Connect to a floor specified by an address string.
	 * Currently supported addressing schemes are: <ul>
	 *
	 *  <li> <code>local:&lt;filename&gt;</code>, where <code>&lt;filename&gt;</code> 
	 *			specifies a dance floor configuration xml file. </li>
	 *  	 
	 *	<li> <code>remote:&lt;address&gt;</code>, where <code>&lt;address&gt;</code> 
	 *			specifies the ip address of a remote host providing a DDF Server. </li>
	 * 			
	 *	<li> <code>virtual:</code>, requesting a virtual dance floor be created on screen. </li>
	 *
	 * </ul>
	 * @param floorAddress the address of the floor to connect.
	 * @throws Exception the floor could not be connected.
	 */
	public static DanceFloor connectFloor(String floorAddress) throws Exception {
		int i = floorAddress.indexOf(":");
		String type = floorAddress.substring(0, i);
		String address = floorAddress.substring(i+1, floorAddress.length());
		
		DanceFloor f;
		
		if (type.equals("virtual")) {
			f = new VirtualFloor();

		} else if (type.equals("local")) {
			f = new LocalFloor(address);
		
		} else if (type.equals("net")) {
			f = new RemoteFloor(address);
		
		} else {
			throw new IllegalArgumentException("Invalid floor protocol: " + floorAddress);
		}
		
		f.connect();
		return f;
	}
	
}

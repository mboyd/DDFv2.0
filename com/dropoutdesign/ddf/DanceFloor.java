package com.dropoutdesign.ddf;

import java.net.InetAddress;

public abstract class DanceFloor {
	
	//public abstract DanceFloor(String address);
	
	public abstract void connect() throws Exception;
	
	public abstract void disconnect();
	
	public abstract boolean isConnected();

	public abstract int getWidth();
	
	public abstract int getHeight();

	public abstract int getFramerate();
	
	public abstract void drawFrame(byte frame[]) throws Exception;
	
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

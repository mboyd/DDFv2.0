package com.dropoutdesign.ddf;

import java.net.InetAddress;

public abstract class DanceFloor {

	public abstract int getWidth();
	
	public abstract int getHeight();

	public abstract int getMaxFPS();
	
	public abstract void drawFrame(byte frame[]) throws Exception;
	
	public static DanceFloor connectFloor(String floorAddress) {
		return null;
	}
	
}

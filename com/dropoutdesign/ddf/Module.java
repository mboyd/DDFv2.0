package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.config.XYCoords;
import com.dropoutdesign.ddf.module.*;
import java.util.*;

public class Module {

	public static final int RETRY_INTERVAL_MS = 500;	//Integer.MAX_VALUE/2;

	private String address;
	private List/*XYCoords*/ layout;
	private ModuleConnection currentConnection = null;
	private long lastFailureTime = 0;
	private boolean badAddress = false;
	
	public Module(String connectAddress, XYCoords[] pixelLayout) {
		address = connectAddress;
		layout = Arrays.asList((XYCoords[])pixelLayout.clone());
	}
	
	public List getPixelCoords() {
		return Collections.unmodifiableList(layout);
	}
	
	public ModuleConnection getConnection() {
		if (badAddress) {
			return null;
		
		} else if (currentConnection != null) {
			return currentConnection;
		
		} else if (lastFailureTime != 0 
			&& (int)(System.currentTimeMillis() - lastFailureTime) < RETRY_INTERVAL_MS) {
				return null;
		}
		
		try {
			currentConnection = ModuleConnection.open(address);
			
			System.out.println(currentConnection.getName() + " firmware: "
					+ Integer.toString(currentConnection.firmwareVersion(), 16));
			System.out.println(currentConnection.getName() + " i2c: "
					+ Integer.toString(currentConnection.checkI2C(), 16));
			
			currentConnection.reset(); // send soft reset command to module
			//System.out.println("Response to self-test: "
			//		+ Integer.toString(currentConnection.selfTest(), 16));
		}
		catch (UnknownConnectionTypeException e) {
			System.err.println(e);
			badAddress = true;
			return null;
		}
		catch (ModuleIOException e) {
			System.err.println(e);
			lastFailureTime = System.currentTimeMillis();
			return null;
		}
		return currentConnection;
	}

	public String getAddress() {
		return address;
	}
	
	public void closeConnection() {
		currentConnection.close();
		currentConnection = null;
	}

	public void errorOccurred() {
		closeConnection(); 
		lastFailureTime = System.currentTimeMillis();
		System.out.println("Error occurred with module at " + address 
			+ "\n\tcurrentConnection=" + currentConnection 
			+ "\n\tlastFailureTime="+ lastFailureTime);
	}
}

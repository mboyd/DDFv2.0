package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.config.*;

import java.util.*;
import java.io.*;
import java.awt.Rectangle;

/**
 * Subclass of LocalFloor that uses ReliableModules to provide automatic error handling.
 */
public class ReliableLocalFloor extends LocalFloor {
	
	private List<ReliableModule> modules;
	
	/**
	 * Initialize a new ReliableLocalFloor.
	 */
	public ReliableLocalFloor(DanceFloorConfig config) {
		super(config);	// Kludge to placate javac
		frameRate = config.framerate;
		
		modules = new ArrayList<ReliableModule>(config.modules.size());
		for (ModuleConfig mc : config.modules) {
			ReliableModule m = new ReliableModule(mc);
		
			Rectangle bounds = m.getBounds();
			int x = bounds.x + bounds.width;
			if (x > width)
				width = x;
			int y = bounds.y + bounds.height;
			if (y > height)
				height = y;
			
			modules.add(m);
		}
		System.out.println("Initialized floor: " + width + "x" + height);
	}
	
	/**
	 * Initialize a new Reliable floor form the specified configuration filename.
	 * @throws IOException the file could not be read or is malformed.
	 */
	public ReliableLocalFloor(String confFile) throws IOException {
		
		this(DanceFloorConfig.readAll(confFile));
	}
	
	/**
	 * Connect to this floor.
	 */
	public void connect() {
		if (isConnected()) {
			return;
		}
		for (ReliableModule m : modules) {
			m.connect();
			numConnectedModules++;
		}
	}
	
	/**
	 * Draw a frame to the floor, attempting to reconnect or reset modules as necessary.
	 */
	public void drawFrame(byte frame[]) {
		for (int i = 0; i < modules.size(); i++) {
			ReliableModule m = modules.get(i);
			m.writeFrame(frame);
		}
	}	
}

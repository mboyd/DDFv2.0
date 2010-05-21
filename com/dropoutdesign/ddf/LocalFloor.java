package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.module.*;

import java.util.*;
import java.io.*;
import java.net.InetAddress;
import java.awt.Rectangle;

public class LocalFloor extends DanceFloor {
	
	private List<Module> modules;
	
	private int width;
	private int height;
	
	private int maxFPS;
	private List<InetAddress> ipWhitelist;
	
	public LocalFloor(String confFile) 
			throws ModuleIOException, UnknownConnectionTypeException, IOException {
		
		this(DanceFloorConfig.readAll(confFile));
	}
	
	public LocalFloor(DanceFloorConfig config) 
			throws ModuleIOException, UnknownConnectionTypeException {
		
		maxFPS = config.maxfps;
		ipWhitelist = config.getWhitelistAddresses();
		
		modules = new ArrayList(config.modules.size());
		for (ModuleConfig mc : config.modules) {
			Module m = new Module(mc);
			m.connect();
		
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

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public List getModules() {
		return modules;
	}

	public int getMaxFPS() {
		return maxFPS;
	}
	
	public void drawFrame(byte frame[]) throws ModuleIOException {
		for (Module m : modules) {
			m.writeFrame(frame);
		}
	}
}

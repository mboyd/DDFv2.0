package com.dropoutdesign.ddf;

import java.util.*;
import com.dropoutdesign.ddf.config.*;

public class DanceFloor {
	
	private int maxFPS;
	private List<Module> modules;
	private List<InetAddress> ipWhitelist;
	private int pixelWidth;
	private int pixelHeight;
	
	public DanceFloor(DanceFloorConfig config) {
		maxFPS = config.maxfps;
		ipWhitelist = config.getWhitelistAddresses();
		modules = new ArrayList(config.modules.size());
		for (ModuleConfig mc : config.modules) {
			modules.add(new Module(mc));
		}
		updateDimensions();
		//System.out.println(pixelWidth+" x "+pixelHeight);
	}

	public int getWidth() {
		return pixelWidth;
	}
	
	public int getHeight() {
		return pixelHeight;
	}
	
	public List getModules() {
		return Collections.unmodifiableList(modules);
	}

	public int getMaxFPS() {
		return maxFPS;
	}
	
	private void updateDimensions() {
		int maxX = 0;
		int maxY = 0;
		for (Iterator iter1 = modules.iterator(); iter1.hasNext();) {
			Module m = (Module)iter1.next();
			for (Iterator iter2 = m.getPixelCoords().iterator(); iter2.hasNext();) {
				XYCoords xy = (XYCoords)iter2.next();
				if (xy.x > maxX) {
					maxX = xy.x;
				}
				if (xy.y > maxY) {
					maxY = xy.y;
				}
			}
		}
		pixelWidth = maxX+1;
		pixelHeight = maxY+1;
	}
}

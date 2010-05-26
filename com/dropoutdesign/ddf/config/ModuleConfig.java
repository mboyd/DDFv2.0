package com.dropoutdesign.ddf.config;

import java.util.regex.*;
import java.awt.Rectangle;

/**
 * Describes the configuration of a DDF Module in an xml-serializable form.
 * This is a structure meant to be converted to and from XML using XStream.
 */

public class ModuleConfig {
	
	/** The address to use for connections to this module. */
	public String address;
	
	/** The origin of this module, in the coordinates of the parent Dance Floor. */
	public String origin;
	
	/** The size of this module, specified as (width, height) in pixels. */
	public String size;
	
	private Rectangle bounds;
	
	/**
	 * Create a new ModuleConfig.
	 * This class is not meant to be instantiated directly; it will instead
	 * be loaded as part of a serialized DanceFloorConfig.
	 */
	public ModuleConfig() {
		address = "";
		origin = "";
		size = "";
	}
	
	/** Return the address of this module. */
	public String getAddress() {
		return address;
	}
	
	/**
	 *  Return the bounds of this module, as a Rectangle in the parent's coordinate space.
	 */
	public Rectangle getBounds() {
		if (bounds == null) {
			Pattern point = Pattern.compile("\\(+(\\d+),(\\d+)\\)+");
			Matcher m = point.matcher(origin);
			if (!m.matches())
				throw new IllegalArgumentException("Invalid origin: " + origin);
			int ox = getNumber(m.group(1));
			int oy = getNumber(m.group(2));
			
			m = point.matcher(size);
			if (!m.matches())
				throw new IllegalArgumentException("Invalid size: " + size);
			int sx = getNumber(m.group(1));
			int sy = getNumber(m.group(2));
			
			bounds = new Rectangle(ox, oy, sx, sy);
		}
		return bounds;
	}
	
	private static int getNumber(String numString) {
		try {
			return Integer.parseInt(numString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("\"" + numString 
				+ "\" is not a number.");
		}	
	}
}

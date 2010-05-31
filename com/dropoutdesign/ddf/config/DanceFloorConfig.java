package com.dropoutdesign.ddf.config;

import java.util.*;
import java.io.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.StreamException;

/**
 * Describes the configuration of a local Disco Dance Floor in an xml-serializable form.
 * This is a structure meant to be converted to and from XML using XStream.
 */

public class DanceFloorConfig {
	
	/** The configuration descriptors of the component modules of this floor. */
	public List<ModuleConfig> modules;
	
	/** The native framerate of this floor. */
	public int framerate;
	
	private static XStream xstream;
	static {
		xstream = new XStream(new DomDriver());
		xstream.alias("dancefloor", DanceFloorConfig.class);
		xstream.alias("module", ModuleConfig.class);
	}
	
	/** 
	 * Create a new DanceFloorConfig.
	 * This class is not intended to be instatiated directly; instead, read a configuration
	 * from disk using <code>DanceFloorConfig.readAll(fileName);</code>
	 */
	public DanceFloorConfig() {
		modules = new ArrayList<ModuleConfig>();
		framerate = -1;
	}
	
	/**
	 * Initialize a DancFloorConfig from a specified file name.
	 * @throws IOException the file could not be read or is malformed.
	 */
	public static DanceFloorConfig readAll(String fileName) throws IOException {
		Reader r = new BufferedReader(new FileReader(new File(fileName)));
		DanceFloorConfig df = readAll(r);
		r.close();
		return df;
	}
	
	private static DanceFloorConfig readAll(Reader r) {
		try {
			return (DanceFloorConfig)xstream.fromXML(r);
		} catch (StreamException e) {
			throw new IllegalArgumentException("Malformed XML: "+ e.getMessage());
		}
	}
	
	/**
	 * Write this DanceFloorConfig as xml to the specified file writer.
	 */
	public void writeAll(Writer w) {
		xstream.toXML(this, w);
	}

	private String writeAll() {
		return xstream.toXML(this);
	}
}

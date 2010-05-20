package com.dropoutdesign.ddf.test;

import com.dropoutdesign.ddf.config.*;
import com.dropoutdesign.ddf.*;

import java.util.Arrays;
import java.io.IOException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Test {
	
	public static void main(String args[]) throws IOException {
		//System.out.println(Arrays.asList(
		//			ModuleConfig.getLEDCoords("0-15,0 0-15,1 0-15,2 0-15,3")));
		//System.out.println(Arrays.asList(ModuleConfig.getLEDCoords("0,0 1,0 2,0")));
		//System.out.println(Arrays.asList(ModuleConfig.getLEDCoords("x63 0,0")));

		//XStream xstream = new XStream(new DomDriver());
		//System.out.println(xstream.fromXML("<foo>apple</"));

		/*DanceFloorConfig dfc = new DanceFloorConfig();
		ModuleConfig m = new ModuleConfig();
		dfc.modules.add(m);
		m.address = "serial:COM4";
		m.layout = "0-15,0 0-15,1 0-15,2 0-15,3";
		System.out.println(dfc.writeAll());*/
	
		DanceFloorConfig dfc = DanceFloorConfig.readAll("config.xml");
		DanceFloor floor = new DanceFloor(dfc);
	}
}

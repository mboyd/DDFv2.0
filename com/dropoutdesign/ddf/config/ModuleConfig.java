package com.dropoutdesign.ddf.config;

import java.util.regex.*;

/**
 * This is a structure meant to be converted to and from XML using XStream.
 */

public class ModuleConfig {
    
    public static final int NUM_LEDS = 64;
    
    public String address;
    public String layout;
    
    public ModuleConfig() {
	address = "";
	layout = "";
    }
    
    public XYCoords[] getLEDCoords() {
	return getLEDCoords(layout);
    }
    
    public static XYCoords[] getLEDCoords(String layoutString) {
	XYCoords coords[] = new XYCoords[NUM_LEDS];
	String words[] = layoutString.trim().split("\\s+");
	Matcher wordSkip = Pattern.compile("x(\\d*)").matcher("");
	Matcher wordSimple = Pattern.compile("(\\d+),(\\d+)").matcher("");
	Matcher wordXRange = Pattern.compile("(\\d+)-(\\d+),(\\d+)").matcher("");
	Matcher wordYRange = Pattern.compile("(\\d+),(\\d+)-(\\d+)").matcher("");
	int curLED = 0;
	for(int i=0;i<words.length;i++) {
	    if (curLED >= NUM_LEDS) {
		throw new IllegalArgumentException("Too may coordinates in layout");
	    }
	    String word = words[i];
	    if (word.length() < 1) continue;
	    Matcher m;
	    if ((m=wordSkip).reset(word).matches()) {
		String strTimes = m.group(1);
		int numTimes;
		if (strTimes.length() == 0) {
		    numTimes = 1;
		}
		else {
		    numTimes = getNumber(strTimes, word);
		}
		curLED += numTimes; // leave nulls for numTimes LEDs
	    }
	    else if ((m=wordSimple).reset(word).matches()) {
		int x = getNumber(m.group(1), word);
		int y = getNumber(m.group(2), word);
		coords[checkLEDNum(curLED++)] = new XYCoords(x,y);
	    }
	    else if ((m=wordXRange).reset(word).matches()) {
		int xStart = getNumber(m.group(1), word);
		int xEnd = getNumber(m.group(2), word);
		int y = getNumber(m.group(3), word);
		for(int x=xStart;x<=xEnd;x++) {
		    coords[checkLEDNum(curLED++)] = new XYCoords(x,y);		    
		}
	    }
	    else if ((m=wordYRange).reset(word).matches()) {
		int x = getNumber(m.group(1), word);
		int yStart = getNumber(m.group(2), word);
		int yEnd = getNumber(m.group(3), word);
		for(int y=yStart;y<=yEnd;y++) {
		    coords[checkLEDNum(curLED++)] = new XYCoords(x,y);		    
		}
	    }
	    else {
		throw new IllegalArgumentException("Bad layout word \""+word+"\"");
	    }
	}
	return coords;
    }
    
    private static int getNumber(String numString, String currentWord) {
	try {
	    return Integer.parseInt(numString);
	}
	catch (NumberFormatException e) {
	    throw new IllegalArgumentException("\""+numString+"\" is not a number in layout word \""+currentWord+"\"");
	}	
    }
    
    private static int checkLEDNum(int curLED) {
	if (curLED >= NUM_LEDS) {
	    throw new IllegalArgumentException("Too may coordinates in layout");
	}
	return curLED;
    }
    
}

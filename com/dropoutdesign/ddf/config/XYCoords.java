package com.dropoutdesign.ddf.config;

public class XYCoords {
    
    public int x,y;
    
    public XYCoords(int xx, int yy) {
	x = xx;
	y = yy;
    }
    
    public String toString() {
	return "("+x+","+y+")";
    }
}

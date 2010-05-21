package com.dropoutdesign.ddf.test;

import com.dropoutdesign.ddf.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

public class SeqTest implements Test {
	
	public boolean test(DanceFloor floor) {
		try {
			System.out.println("Running seqTest (what does this do, anyway?)...");
			
			int width = floor.getWidth();
			int height = floor.getHeight();
		
			byte[] pixels = new byte[width*height*3];
		
			for (int i = 0; i < 100; i++) {
				for (int t = 0; t < 24; t++) {
					int b = 0;
					for (int y = 0; y < height; y++) {
						for (int x = 0; x < width; x++) {
							for (int c=0;c<3;c++) {
								pixels[b] = ((b%24) == t ? (byte)0xFF : (byte)0x00);
								b++;
							}
						}
					}
					floor.drawFrame(pixels);
				}
			}
			System.out.println("Done.");
			return true;

		} catch (Exception e) {
			System.out.println("Test failed: " + e);
			e.printStackTrace();
			return false;
		}
	}
}

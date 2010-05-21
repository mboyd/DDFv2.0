package com.dropoutdesign.ddf.test;

import com.dropoutdesign.ddf.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.lang.Thread;

public class StrobeTest {
	
	public boolean test(VirtualFloor floor) {
		//try {
			System.out.println("Strobing floor... ");
			
			int width = floor.getWidth();
			int height = floor.getHeight();
			
			byte[] frame = new byte[width*height*3];
		
			for (int i = 0; i < 60; i++) {

				for (int l = 0; l < frame.length; l++) {
					frame[l] = (byte)(((i%2) == 0 ? 1:0)*0xff);
				}
				
				long t1 = System.currentTimeMillis();
				
				floor.drawFrame(frame);
				
				long t2 = System.currentTimeMillis();
				System.out.println("\tRedrawn in " + (t2-t1) + "ms.");
				
				try { Thread.sleep(500); } catch (InterruptedException e) {}
			}
			
			System.out.println("Done.");
			return true;
			
		/*} catch (Exception e) {
			System.out.println("Test failed: " + e);
			e.printStackTrace();
			return false;
		}*/
	}
}

package com.dropoutdesign.ddf;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

public class VirtualFloor extends DanceFloor {
	
	private int width = 16;
	private int height = 32;
	
	private int FRAMERATE = 15;
	private int FRAMETIME = 1000 / FRAMERATE;
	private long lastRedraw;
	
	private JFrame myFrame;
	private JPanel myPanel;
	private JLabel imgLabel;
	private Image currentFrame;
	
	ColorModel cm;
	SampleModel sm;

	public VirtualFloor(){
		cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
							false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
		
		int[] offsets = {0,1,2};
		sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, 3*width, offsets);
		
		createGUI();
		
		lastRedraw = System.currentTimeMillis();
	}
	
	private void createGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		myFrame = new JFrame("DDF");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		myPanel = new JPanel(new BorderLayout());
		
		myFrame.setContentPane(myPanel);
		myFrame.setBounds(20,20,width*10,height*10);
		myFrame.setResizable(false);
		myFrame.setVisible(true);
	}
	
	
	public void drawFrame(byte frame[]) {
		
		DataBuffer db = new DataBufferByte(frame, width*height);
		WritableRaster r = Raster.createWritableRaster(sm, db, new Point(0,0));
		
		Image newFrame = new BufferedImage(cm, r, false, null);
		currentFrame = newFrame.getScaledInstance(width*10, height*10, Image.SCALE_DEFAULT);
				
		imgLabel = new JLabel(new ImageIcon(currentFrame));
		myPanel.removeAll();
		myPanel.add(imgLabel);
		myPanel.validate();
		myFrame.repaint();
		
		long t = System.currentTimeMillis();
		long delta = t - lastRedraw;
		if (delta < FRAMETIME) {
			try { Thread.sleep(FRAMETIME - delta); } catch (InterruptedException e) {}
		}
		lastRedraw = System.currentTimeMillis();
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getMaxFPS() {
		return FRAMERATE;
	}
	
	public boolean isConnected() {
		return true;
	}
	
	public void connect(String serverAddress) {
		return;
	}
	
	public void disconnect() {
		return;
	}
}

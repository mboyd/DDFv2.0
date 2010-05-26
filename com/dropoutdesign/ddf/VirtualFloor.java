package com.dropoutdesign.ddf;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;

/**
 * Disco Dance Floor Simulator.
 * Provides a DanceFloor instance that renders to an on-screen window.
 */
public class VirtualFloor extends DanceFloor {
	
	private int width = 16;
	private int height = 32;
	private boolean connected;
	
	private int FRAMERATE = 5;
	private int FRAMETIME = 1000 / FRAMERATE;
	private long lastRedraw;
	
	private JFrame myFrame;
	private JPanel myPanel;
	private JLabel imgLabel;
	private Image currentFrame;
	
	ColorModel cm;
	SampleModel sm;

	/**
	 * Create a new virtual floor.
	 * The window will be created immediately, but the floor starts in a disconnected state.
	 */
	public VirtualFloor() {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", 
							"DDF Virtual Floor");
		
		cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB),
							false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
		
		int[] offsets = {0,1,2};
		sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, 3*width, offsets);
		
		createGUI();
		
		lastRedraw = System.currentTimeMillis();
		connected = false;
	}
	
	/**
	 * Crate a new virtual floor.
	 * @param address unused.
	 */
	public VirtualFloor(String address) {
		this();
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
	
	/**
	 * Render a frame to screen.
	 * @param frame the frame to draw.
	 * @throws IOException never.
	 * @see com.dropoutdesign.ddf.DanceFloor#drawFrame
	 */
	public void drawFrame(byte frame[]) throws IOException {
		
		if (!connected)
			throw new IOException("Virtual floor not connected (virtually)");
		
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
	
	/**
	 * Get the width of this floor.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of this floor.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Get the native framerate of this floor.
	 */
	public int getFramerate() {
		return FRAMERATE;
	}
	
	/**
	 * Return the connection status of this floor.
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Connect to this floor.
	 */
	public void connect() {
		connected = true;
	}
	
	/**
	 * Disconnect from this floor.
	 */
	public void disconnect() {
		connected = false;
	}
}

package com.dropoutdesign.ddf.client;

import com.dropoutdesign.ddf.*;

import java.io.IOException;

public class PublishThread extends Thread {
	
	private RemoteFloor myFloor;
	private int frameTime;
	private long lastRedraw;
	
	private boolean switchPattern;
	private boolean stop;
	private boolean pause;
	private byte[][] data;
	private int numframes;
	
	public PublishThread(String str, RemoteFloor myF) {
		super(str);
		myFloor = myF;
		frameTime = 1000 / myFloor.getFramerate();
		lastRedraw = System.currentTimeMillis();
		
		switchPattern = false;
		stop = false;
		pause = false;
	}

	public void setSwitchPattern(boolean b) {
		switchPattern = b;
	}
	
	public void setStop(boolean b) {
		stop = b;
	}
	
	public void setPause(boolean b) {
		pause = b;
	}
	
	public void run() {
		while(!stop) {
				while(!switchPattern && !stop ) {
					try {
						for(int i = 0; i < (numframes-1) && !switchPattern && !stop; i++) { 
						    if (ClientGUI.playMode == 3) {
								// Hack to do midnight countdown
								long time = System.currentTimeMillis() / 1000;
								long target = 1260075600; // midnight sunday
								System.out.println("time="+time
										+",target="+target+",diff="+(target-time));
								
								if ((target - time) > 60)
								    i = 60;
								else if (target >= time) {
								    i = (int)(target-time);
								} else {
								    i = (int)(61+((time-target)%3));
								}
						    }
							
							if(!myFloor.isConnected()) {
								stop = true;
								System.out.println("[Publish] Lost connection to floor, stopping.");
							
							} else if(!pause) {
								//System.out.println("[Publish] Drawing frame " + i 
								//	+ " of " + numframes);
								
								try {
									myFloor.drawFrame(data[i]);
								} catch (Exception e) {
									System.out.println("[Publish] Lost floor connection.");
								}
								
								long t = System.currentTimeMillis();
								long delta = t - lastRedraw;
								if (delta < frameTime) {
									try { Thread.sleep(frameTime - delta); } catch (Exception e) {}
								} else {
									System.out.println("[Publish] Buffer underrun.");
								}
								lastRedraw = System.currentTimeMillis();
							
							} else {	// Paused
								try { Thread.sleep(200);} catch (InterruptedException e) {}
								i--;
							}
						}
					} catch (Exception e) {
						myFloor.disconnect();
						System.out.println("[Publish] IO error, disconnecting.");
						e.printStackTrace();
					}
				}
				data = LoadThread.getData();
				numframes = LoadThread.getNumFrames();
				switchPattern = false;
			}
		stop = true;
	}
}
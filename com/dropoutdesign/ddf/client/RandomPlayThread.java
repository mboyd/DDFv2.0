package com.dropoutdesign.ddf.client;

public class RandomPlayThread extends Thread {
	private int seconds;
	private Playable myP;
	private String[] myFNames;

	
	public RandomPlayThread(String s, int sec, Playable p, String[] fNames) {
		super(s);
		seconds = sec;
		myP = p;
		myFNames = fNames;
	}
	
	public void run() {
		while(ClientGUI.playMode == 2) {
			playRandomAnimation();
			
			try { Thread.sleep(seconds*1000l); } catch (InterruptedException e) {}
		}
	}
	
	public void playRandomAnimation() {
		int rand = (int)(Math.random()*myFNames.length);
		myP.playAnimation(myFNames[rand]);
	}
}
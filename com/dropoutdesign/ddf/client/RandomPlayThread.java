package com.dropoutdesign.ddf.client;

public class RandomPlayThread extends Thread
{
	private int seconds;
	private Playable myP;
	private String[] myFNames;

	
	public RandomPlayThread(String s, int sec, Playable p, String[] fNames)
	{
		super(s);
		seconds = sec;
		myP = p;
		myFNames = fNames;
	}
	
	public void run()
	{
		while(ClientGUI.playMode == 2)
		{
			playRandomAnimation();
			try {
				Thread.sleep((long)(seconds*1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void playRandomAnimation()
	{
		int rand = (int)(Math.random()*myFNames.length);
		myP.playAnimation(myFNames[rand]);
	}
}
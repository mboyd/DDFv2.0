package com.dropoutdesign.ddf.client;

import java.io.IOException;

public class PublishThread extends Thread
{
	private FloorWriter myFloor;
	private boolean switchPattern;
	private boolean stop;
	private boolean pause;
	private byte[][] data;
	private int numframes;
	
	public PublishThread(String str, FloorWriter myF)
	{
		super(str);
		myFloor = myF;
		switchPattern = false;
		stop = false;
		pause = false;
	}

	public void setSwitchPattern(boolean b)
	{
		switchPattern = b;
	}
	
	public void setStop(boolean b)
	{
		stop = b;
	}
	
	public void setPause(boolean b)
	{
		pause = b;
	}
	public void run()
	{
		while(!stop)
			{
				while(!switchPattern && !stop )
				{
					try {
						for(int i = 0; i < (numframes-1) && !switchPattern && !stop; i++)
						{ 
						    if (ClientGUI.playMode == 3) {
								// Hack to do midnight countdown
								long time = System.currentTimeMillis() / 1000;
								long target = 1260075600; // midnight sunday
								System.out.println("time="+time+",target="+target+",diff="+(target-time));
								if ((target - time) > 60)
								    i = 60;
								else if (target >= time) {
								    i = (int)(target-time);
								} else {
								    i = (int)(61+((time-target)%3));
								}
						    }
							if(myFloor.isClosed())
								stop = true;
							else if(!pause)
								myFloor.waitAndSend(data[i]);
							else
							{
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								i--;
							}
						}
					} catch (IOException e) {
						myFloor.disconnect();
						// TODO Auto-generated catch block
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
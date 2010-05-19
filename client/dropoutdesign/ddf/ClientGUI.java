package client.dropoutdesign.ddf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.UnknownHostException;
import java.net.*;

public class ClientGUI
{
	private FloorWriter myFloor;
	private Player myPlayer;
	
	private JFrame myFrame;
	private JPanel myPanel;
	private JList myList;
	private JButton playAnimation;
	private JButton pauseAnimation;
	private JButton stopAnimation;
	private JMenu modes;
	private JMenuItem connectToFloor;
	
	
	//Threads
	private LoadThread myAnimationLoader;
	private PublishThread myPublishThread;

	public static int playMode; // 1 is countinous Play
								 // 2 is RandomPlay
    // 3 is CountdownPlay
	
	public ClientGUI()
	{
		setLAF();
		playMode = 1;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
	}
	
	public void setLAF()
	{
		if(UIManager.getSystemLookAndFeelClassName().equals("javax.swing.plaf.mac.MacLookAndFeel"))
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void createGUI()
	{
		
		myPlayer = new Player();
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		myFrame = new JFrame("Disco Dance Floor Client");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		myPanel = new JPanel(new BorderLayout());
		myPanel.add(createAnimationList(), BorderLayout.LINE_START);
		
		myFrame.setJMenuBar(createMenuBar());
		
		JPanel playControls = new JPanel(new BorderLayout());
		
		playAnimation = new JButton();//"Play");
			playAnimation.setIcon(new ImageIcon(ClientGUI.class.getResource("images/play.png")));
			//playAnimation.setVerticalTextPosition(SwingConstants.BOTTOM);
		    //playAnimation.setHorizontalTextPosition(SwingConstants.CENTER);
			playAnimation.setEnabled(false);
			playAnimation.setSize(220,220);
			playAnimation.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{	if(myFloor.isClosed())
						closeConnection();
					else if(myList.getSelectedValue() != null)
						myPlayer.playAnimation(myList.getSelectedValue().toString());
						
				}
			});
			playControls.add(playAnimation, BorderLayout.CENTER);
			
		pauseAnimation = new JButton();//"Pause");
			pauseAnimation.setIcon(new ImageIcon(ClientGUI.class.getResource("images/pause.png")));
			//pauseAnimation.setVerticalTextPosition(SwingConstants.BOTTOM);
		    //pauseAnimation.setHorizontalTextPosition(SwingConstants.CENTER);
			pauseAnimation.setEnabled(false);
			pauseAnimation.setSize(220,220);
			pauseAnimation.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					if(myFloor.isClosed())
						closeConnection();
					else if(myList.getSelectedValue() != null)
						myPlayer.pauseAnimation();
						
				}
			});
			playControls.add(pauseAnimation, BorderLayout.WEST);
		
		stopAnimation = new JButton();//"Stop");
			stopAnimation.setIcon(new ImageIcon(ClientGUI.class.getResource("images/stop.png")));
		   // stopAnimation.setVerticalTextPosition(SwingConstants.BOTTOM);
		   // stopAnimation.setHorizontalTextPosition(SwingConstants.CENTER);
			stopAnimation.setEnabled(false);
			stopAnimation.setSize(220,220);
			stopAnimation.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					if(myFloor.isClosed())
						closeConnection();
					else if(myList.getSelectedValue() != null)
						myPlayer.stopAnimation();
						
				}
			});
			playControls.add(stopAnimation, BorderLayout.EAST);
		
		myPanel.add(playControls, BorderLayout.CENTER);
		myFrame.setContentPane(myPanel);
		myFrame.setBounds(0,0,1024,300);
		myFrame.setVisible(true);
		
	}

	public void intialConnect()
	{
		ConnectorThread danceFloorConnector = new ConnectorThread("ConnectorThread", "dancefloor.mit.edu");
		danceFloorConnector.start();
	}
	
	private JScrollPane createAnimationList()
	{
		myList = new JList(getFileNames());
		myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(myList);
		return scrollPane;
	}
	

	
	private String[] getFileNames()
	{
		File curDir = new File(".");
		File[] animFiles = curDir.listFiles(new FileFilter()
				{
			public boolean accept(File f)
			{
				return f.getName().contains(".ddf");
			}
				});
		String[] fNames = new String[animFiles.length];
		String s;
		for(int i = 0; i < animFiles.length; i++)
		{
			s = animFiles[i].getName();
			fNames[i] = s.substring(0,s.indexOf(".ddf"));
		}
		return fNames;
		
	}
	/**
	 * 
	 */
	private JMenuBar createMenuBar() {
		JMenuBar myMenuBar = new JMenuBar();
			JMenu connections = new JMenu("Connections");
				connectToFloor = new JMenuItem("Connect...");
				connectToFloor.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							createConnectionDialog();
						}
				});
				disconnected();
				connections.add(connectToFloor);
				
				JMenuItem disconnectFromFloor = new JMenuItem("Disconnect");
				disconnectFromFloor.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						closeConnection();
						System.out.println("1st disconnect completed");
						System.out.println("status: " + myFloor.isClosed());
					}
			});
				connections.add(disconnectFromFloor);
			
			myMenuBar.add(connections);
			
			modes = new JMenu("Mode");
				JRadioButtonMenuItem continuousPlay = new JRadioButtonMenuItem("Countinous Play", true);
				continuousPlay.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e)
					{
						if(e.getStateChange() == ItemEvent.SELECTED)
						{
						    playMode = 1;
						}
					}
				    });
				modes.add(continuousPlay);
				
				JRadioButtonMenuItem randomPlay = new JRadioButtonMenuItem("Random Play");
				randomPlay.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e)
					{
						if(e.getStateChange() == ItemEvent.SELECTED)
						{
							playMode = 2;
							String time = JOptionPane.showInputDialog("How long between animation switches (seconds)", "180");
							RandomPlayThread myRanThread = new RandomPlayThread("RandomPlayThread", Integer.parseInt(time), myPlayer, getFileNames());
							myRanThread.start();
						}
					}
				});
				modes.add(randomPlay);

				JRadioButtonMenuItem countdownPlay = new JRadioButtonMenuItem("Countdown Play");
				countdownPlay.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e)
					{
						if(e.getStateChange() == ItemEvent.SELECTED)
						{
						    playMode = 3;
						}
					}
				});
				modes.add(countdownPlay);

				/* NB: This is here to ensure that only one button is checked at a time */
				ButtonGroup playModes = new ButtonGroup();
				playModes.add(continuousPlay);
				playModes.add(randomPlay);
				playModes.add(countdownPlay);

				modes.setEnabled(false);
			myMenuBar.add(modes);
		return myMenuBar;
	}

	class ConnectorThread extends Thread
	{
		public String hostName;
		public ConnectorThread(String str, String hName)
		{
			super(str);
			hostName = hName;
		}
		
		  public void run() {
		      if(myFloor == null)
		      {
		    	  try {
					myFloor = new FloorWriter(hostName);
					playAnimation.setEnabled(true);
					pauseAnimation.setEnabled(true);
					stopAnimation.setEnabled(true);
					modes.setEnabled(true);
					connected();
				} catch (UnknownHostException e) {
					disconnected();
					JOptionPane.showMessageDialog(null,
						    "Could not connect to Floor",
						    "Connection Error",
						    JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					disconnected();
					JOptionPane.showMessageDialog(null,
						    "Could not connect to Floor",
						    "Connection Error",
						    JOptionPane.ERROR_MESSAGE);
				}
		      }
		      else{
		    	  if(!myFloor.isClosed())
		    		  closeConnection();
		    	  try {
					myFloor.connect(hostName);
					playAnimation.setEnabled(true);
					pauseAnimation.setEnabled(true);
					stopAnimation.setEnabled(true);
					modes.setEnabled(true);
					connected();
				} catch (UnknownHostException e) {
					disconnected();
					JOptionPane.showMessageDialog(null,
						    "Could not connect to Floor",
						    "Connection Error",
						    JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					disconnected();
					JOptionPane.showMessageDialog(null,
						    "Could not connect to Floor",
						    "Connection Error",
						    JOptionPane.ERROR_MESSAGE);
				} 
		      }
		      
		    }
	}
	
	
	private void createConnectionDialog()
	{
		String hostName = JOptionPane.showInputDialog("Input Server Hostname", "dancefloor.mit.edu");
		
		ConnectorThread danceFloorConnector = new ConnectorThread("ConnectorThread", hostName);
		danceFloorConnector.start();
	}
	
	
	private void connected()
	{
		connectToFloor.setIcon(new ImageIcon(ClientGUI.class.getResource("images/conn.gif")));
		//connectToFloor.setIcon(new ImageIcon("images/conn.gif"));
	}
	private void disconnected()
	{
		connectToFloor.setIcon(new ImageIcon(ClientGUI.class.getResource("images/disc.gif")));
		//connectToFloor.setIcon(new ImageIcon("images/disc.gif"));
	}
	private void closeConnection()
	{
		if(myPublishThread != null)
		{
			myPublishThread.setStop(true);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myPublishThread = null;
			myAnimationLoader = null;
		}
		if(myFloor != null)
			myFloor.disconnect();
		playAnimation.setEnabled(false);
		pauseAnimation.setEnabled(false);
		stopAnimation.setEnabled(false);
		modes.setEnabled(false);
		disconnected();
	}
	
	class Player implements Playable{
		private boolean isPaused = false;
		public void playAnimation(String s)
		{
			if(myAnimationLoader == null)
			{
				myPublishThread = new PublishThread("PublishThread", myFloor);
				myAnimationLoader = new LoadThread("LoadThread", s, myPublishThread, myFloor);
				myAnimationLoader.start();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myPublishThread.start();
			}
			else if(!isPaused){
				myAnimationLoader = new LoadThread("LoadThread", s, myPublishThread, myFloor);
				myAnimationLoader.start();
			}
			else
			{
				myPublishThread.setPause(false);
				isPaused = false;
			}
		}
		
		public void pauseAnimation()
		{
			if(!isPaused){
			myPublishThread.setPause(true);
			isPaused = true;
			}
			else
			{
				myPublishThread.setPause(false);
				isPaused = false;
			}
		}
		public void stopAnimation()
		{
			myPublishThread.setStop(true);
			myAnimationLoader = null;
		}
	}
	
	
}
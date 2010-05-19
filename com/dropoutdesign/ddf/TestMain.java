package com.dropoutdesign.ddf;

import com.dropoutdesign.ddf.module.*;
import java.util.*;

public class TestMain {
	
	public static void main(String args[]) throws ModuleConnectionException {
		//ModuleConnection connection = 
		//		ModuleConnection.open("serial:/dev/tty.usbserial-3B111");
		
		List connections = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			connections.add(ModuleConnection.open("serial:" + args[i]));
		}

		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			ModuleConnection mc = (ModuleConnection)iter.next();
			mc.reset();
		}
	
		byte writeCommand[] = moduleCmd(null, 0);

		List frameTimes = new ArrayList();
		for (int n = 0; n < 4; n++) {
			for (int i = 0; i < 96; i++) {
				long startTime = System.currentTimeMillis();
				moduleCmd(writeCommand, i);
				for(int k = 0; k < connections.size(); k++) {
					ModuleConnection mc = (ModuleConnection)connections.get(k);
					mc.sendCommand(writeCommand);
				}
				boolean stillWaiting = true;
				while (stillWaiting) {
					stillWaiting = false;
					for (int k = 0; k < connections.size(); k++) {
						ModuleConnection mc = (ModuleConnection)connections.get(k);
						if (mc.tryReadResponse() < 0) {
							stillWaiting = true;
							break;
						}
					}
					if (stillWaiting) {
						try { Thread.sleep(1); } catch (InterruptedException e) {}
					}
				}
				frameTimes.add(new Integer((int)(System.currentTimeMillis() - startTime)));
			}
		}
	
		for(Iterator iter = connections.iterator(); iter.hasNext();) {
			ModuleConnection mc = (ModuleConnection)iter.next();
			mc.blackout();
			mc.close();
		}

		System.out.println(frameTimes);
	
	
		for(int a = 0; a < args.length; a++) {
			ModuleConnection connection = ModuleConnection.open("serial:"+args[a]);
			byte cmd[] = null;
			//connection.sendCommand(0x60);
			//connection.sendCommand(0xee);
			long startTime = System.currentTimeMillis();
			for(int i = 1; i <= 96; i++) {
				cmd = moduleCmd(cmd, i);
				connection.sendCommand(cmd);
				byte response[];
				connection.tryReadResponse();
			}
			System.out.println(System.currentTimeMillis() - startTime);
			connection.sendCommand(0x40);
			connection.close();
		}
	}
				   
	private static byte[] moduleCmd(byte cmd[], int whichByte) {
		if (cmd == null) cmd = new byte[97];
		cmd[0] = 0x10;
		for (int i = 1; i <= 96; i++) {
			if (i == whichByte) {
				cmd[i] = (byte)0x00;
			} else {
				cmd[i] = (byte)0xff;
			}
		}
		return cmd;
	}
}

package com.dropoutdesign.ddf.module;

import java.io.*;

public abstract class ModuleConnection {
	
	public static final int MAX_RESPONSE_BYTES = 10;
	public static final int TIMEOUT_MS = 60;
	
	private OutputStream outStream;
	private InputStream inStream;
	private String name;
	
	private byte responseBytes[] = new byte[MAX_RESPONSE_BYTES];
	private int numReceivedBytes = 0;

	private boolean commandInProgress = false;
	private int numBytesExpected;
	private boolean usesStatusByte;
	
	protected ModuleConnection(String name) {
		this.name = name;
	}

	protected void init(OutputStream outStream, InputStream inStream) {
		this.outStream = outStream;
		this.inStream = inStream;
	}
	
	/**
	 * Open a connection given a string such as "serial:COM4" that
	 * specifies a protocol and a location such as a port name or IP
	 * address.	 The connection must be closed using the close()
	 * method when it is no longer needed.
	 */
	public static ModuleConnection open(String connectString)
				throws ModuleIOException, UnknownConnectionTypeException {
	
		int colonIndex = connectString.indexOf(':');
		if (colonIndex < 1) {
			throw new UnknownConnectionTypeException("Connection string doesn't " 
					 +" begin with a protocol followed by a colon.");
		}
		String protocol = connectString.substring(0,colonIndex);
		String location = connectString.substring(colonIndex+1);
		if (SerialConnection.PROTOCOL.equalsIgnoreCase(protocol)) {
			return new SerialConnection(location);
		} else {
			throw new UnknownConnectionTypeException("Unknown protocol \""+protocol+"\".");
		}
	}

	private void clearInput() {
		try {
			if (inStream.available() > 0) {
				inStream.skip(inStream.available());
			}
		} catch (IOException io) {}
	}
	
	public void sendCommand(byte cmd[]) throws ModuleIOException {
		clearInput();
		numReceivedBytes = 0;
		try {
			outStream.write(cmd);
		} catch (IOException io) {
			throw new ModuleIOException(io);
		}
		commandInProgress = true;
		numBytesExpected = getNumBytesExpected(((int)cmd[0])&0xFF);
		usesStatusByte = getUsesStatusByte(((int)cmd[0])&0xFF);
	}

	public void sendCommand(int cmd) throws ModuleIOException {
		clearInput();
		numReceivedBytes = 0;
		try {
			outStream.write(cmd);
		} catch (IOException io) {
			throw new ModuleIOException(io);
		}
		commandInProgress = true;
		numBytesExpected = getNumBytesExpected(((int)cmd)&0xFF);
		usesStatusByte = getUsesStatusByte(((int)cmd)&0xFF);
	}

	public byte ping() throws ModuleIOException {
		sendCommand(0x50);
		return readResponseByte();
	}

	public byte blackout() throws ModuleIOException {
		sendCommand(0x40);
		return readResponseByte();
	}

	public byte reset() throws ModuleIOException {
		sendCommand(0x60);
		return readResponseByte();
	}

	public byte selfTest() throws ModuleIOException {
		sendCommand(0x80);
		return readResponseByte();
	}

	public byte firmwareVersion() throws ModuleIOException {
		sendCommand(0x70);
		readResponsePrivate();
		if (responseBytes[0] != 0) { // for old firmware
			return responseBytes[0];
		}
		else {
			return responseBytes[1];	
		}
	}

	public int checkI2C() throws ModuleIOException {
		sendCommand(0x61);
		readResponsePrivate();
		if (responseBytes[0] != 0) { // for old firmware
			return -1;
		}
		else {
			return ((((int)responseBytes[1])&0xFF)<<8) | (((int)responseBytes[2])&0xFF);
		}
	}
	
	private void readResponsePrivate() throws ModuleIOException {
		try {
			takeBytes();
			long startTime = System.currentTimeMillis();
			while (commandInProgress) {
				if ((int)(System.currentTimeMillis() - startTime) > TIMEOUT_MS) {
					throw new TimeoutException("Command timed out on port "+ getName());
				}
				try { Thread.sleep(2); } catch (InterruptedException e) {}
				takeBytes();
			}
		}
		catch (IOException io) {
			throw new ModuleIOException(io);
		}
	}

	public byte readResponseByte() throws ModuleIOException {
		readResponsePrivate();
		return responseBytes[0];	
	}

	public void readResponse(byte destArray[]) throws ModuleIOException {
		readResponse(destArray, 0);
	}
	
	public void readResponse(byte destArray[], int startOffset) throws ModuleIOException {
		readResponsePrivate();
		System.arraycopy(responseBytes, 0, destArray, startOffset, numReceivedBytes);
	}
	
	public boolean isBusy() throws ModuleIOException {
		try {
			takeBytes();
		} catch (IOException io) {
			throw new ModuleIOException(io);
		}
		return commandInProgress;
	}

	public int tryReadResponse() throws ModuleIOException {
		if (!isBusy()) {
			return (int)responseBytes[0];
		}
		return -1;
	}
	
	public boolean tryReadResponse(byte destArray[]) throws ModuleIOException {
		return tryReadResponse(destArray, 0);
	}
	
	public boolean tryReadResponse(byte destArray[], int startOffset) throws ModuleIOException {
		if (!isBusy()) {
			System.arraycopy(responseBytes, 0, destArray, startOffset, numReceivedBytes);
			return true;
		}
		return false;
	}

	public void close() {
		try {
			inStream.close();
			outStream.close();
		} catch (IOException io) {
			// ignore exception when closing
		}
	}

	public String getName() {
		return name;
	}

	private int getNumBytesExpected(int cmd) {
		switch (cmd) {
		case 0x20: return 9;
		case 0x30: return 9;
		case 0x70: return 2;
		case 0x61: return 3;
		default: return 1;
		}
	}
	
	private boolean getUsesStatusByte(int cmd) {
		//return (cmd & 0xF0) != 0x70;
		return true;
	}
	
	private boolean responseComplete() {
		return numReceivedBytes > 0 
			&& ((numReceivedBytes == numBytesExpected) 
			|| (usesStatusByte && responseBytes[0] != 0x00));
	}
	
	private void takeBytes() throws IOException {
		while (inStream.available() > 0 && !responseComplete()) {
			responseBytes[numReceivedBytes++] = (byte)inStream.read();
		}
		if (responseComplete()) {
			commandInProgress = false;
		}
	}
}

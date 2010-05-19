package com.dropoutdesign.ddf.module;

/**
 * An exception that occurs when talking to a module that is due to a
 * problem at the transport layer or I/O stream layer.
 */

public class ModuleIOException extends ModuleConnectionException {

	public ModuleIOException() {
		super();
	}

	public ModuleIOException(Throwable cause) {
		super(cause);
	}
	
	public ModuleIOException(String message) {
		super(message);
	}
	
	public ModuleIOException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

package com.dropoutdesign.ddf.module;

/**
 * An exception that occurs when trying to communicate with a module.
 */

public class ModuleConnectionException extends Exception {

	public ModuleConnectionException() {
		super();
	}

	public ModuleConnectionException(Throwable cause) {
		super(cause);
	}
	
	public ModuleConnectionException(String message) {
		super(message);
	}
	
	public ModuleConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

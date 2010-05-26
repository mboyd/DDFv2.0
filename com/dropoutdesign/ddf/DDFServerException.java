package com.dropoutdesign.ddf;

/**
 * An exception encountered by a DDFServer.
 */
public class DDFServerException extends Exception {

	public DDFServerException() {
		super();
	}

	public DDFServerException(Throwable cause) {
		super(cause);
	}
	
	public DDFServerException(String message) {
		super(message);
	}
	
	public DDFServerException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

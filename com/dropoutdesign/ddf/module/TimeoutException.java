package com.dropoutdesign.ddf.module;

/**
 * An exception that indicates that too much time passed while receiving bytes from a module.
 */

public class TimeoutException extends ModuleIOException {

    public TimeoutException() {
	super();
    }

    public TimeoutException(Throwable cause) {
	super(cause);
    }
    
    public TimeoutException(String message) {
	super(message);
    }
    
    public TimeoutException(String message, Throwable cause) {
	super(message, cause);
    }
    
}

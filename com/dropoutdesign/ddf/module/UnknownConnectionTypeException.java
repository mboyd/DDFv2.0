package com.dropoutdesign.ddf.module;

/**
 * An exception that occurs when a connection string that invokes an
 * unknown protocol is encountered.
 */

public class UnknownConnectionTypeException extends ModuleConnectionException {

    public UnknownConnectionTypeException() {
	super();
    }

    public UnknownConnectionTypeException(Throwable cause) {
	super(cause);
    }
    
    public UnknownConnectionTypeException(String message) {
	super(message);
    }
    
    public UnknownConnectionTypeException(String message, Throwable cause) {
	super(message, cause);
    }
    
}

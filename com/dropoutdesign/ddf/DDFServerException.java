package com.dropoutdesign.ddf;

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

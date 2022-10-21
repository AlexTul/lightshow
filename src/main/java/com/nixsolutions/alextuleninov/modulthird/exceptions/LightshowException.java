package com.nixsolutions.alextuleninov.modulthird.exceptions;

public class LightshowException extends Exception{
    public LightshowException() {
        super();
    }

    public LightshowException(String message) {
        super(message);
    }

    public LightshowException(String message, Throwable cause) {
        super(message, cause);
    }

    public LightshowException(Throwable cause) {
        super(cause);
    }
}

package com.ljtao3.exception;

public class MyPermissionException extends RuntimeException {
    public MyPermissionException() {
        super();
    }

    public MyPermissionException(String message) {
        super(message);
    }

    public MyPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyPermissionException(Throwable cause) {
        super(cause);
    }

    protected MyPermissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

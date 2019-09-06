package com.ljtao3.http.ext;

/**
 * Created by jimin on 16/03/10.
 */
public class AuthSSLInitializationError extends Error {

    public AuthSSLInitializationError() {
    }

    public AuthSSLInitializationError(String message) {
        super(message);
    }

    public AuthSSLInitializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthSSLInitializationError(Throwable cause) {
        super(cause);
    }
}

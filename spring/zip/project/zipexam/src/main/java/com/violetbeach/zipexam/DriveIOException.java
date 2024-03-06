package com.violetbeach.zipexam;

public class DriveIOException extends RuntimeException {
    public DriveIOException(String message) {
        super(message);
    }

    public DriveIOException(Throwable cause) {
        super(cause);
    }
}

package com.arishi.AXAM.exception;

public class AlreadyPassedException extends RuntimeException {
    public AlreadyPassedException(String message) {
        super(message);
    }
}
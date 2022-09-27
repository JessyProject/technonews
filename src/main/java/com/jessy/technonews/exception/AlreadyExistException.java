package com.jessy.technonews.exception;

public class AlreadyExistException extends IllegalArgumentException {
    public AlreadyExistException(String msg) {
        super(msg);
    }
}

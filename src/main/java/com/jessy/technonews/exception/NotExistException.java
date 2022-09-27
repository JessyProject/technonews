package com.jessy.technonews.exception;

public class NotExistException extends IllegalArgumentException {
    public NotExistException(String msg) {
        super(msg);
    }
}

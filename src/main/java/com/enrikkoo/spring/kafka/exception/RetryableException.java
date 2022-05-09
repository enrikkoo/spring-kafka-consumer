package com.enrikkoo.spring.kafka.exception;

public class RetryableException extends RuntimeException{

    public RetryableException(String msg) {
        super(msg);
    }
}

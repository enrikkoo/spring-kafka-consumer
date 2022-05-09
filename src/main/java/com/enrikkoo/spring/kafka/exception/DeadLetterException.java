package com.enrikkoo.spring.kafka.exception;

public class DeadLetterException extends RuntimeException{

    public DeadLetterException(String msg) {
        super(msg);
    }
}

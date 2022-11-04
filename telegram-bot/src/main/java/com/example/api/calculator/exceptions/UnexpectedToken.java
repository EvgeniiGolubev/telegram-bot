package com.example.api.calculator.exceptions;

public class UnexpectedToken extends RuntimeException {
    public UnexpectedToken(String message) {
        super(message);
    }
}

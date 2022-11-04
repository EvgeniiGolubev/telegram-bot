package com.example.api.calculator.exceptions;

public class UnexpectedCharacter extends RuntimeException {
    public UnexpectedCharacter(String message) {
        super(message);
    }
}

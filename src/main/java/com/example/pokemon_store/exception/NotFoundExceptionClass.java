package com.example.pokemon_store.exception;

public class NotFoundExceptionClass extends RuntimeException {
    public NotFoundExceptionClass(String message) {
        super(message);
    }
}

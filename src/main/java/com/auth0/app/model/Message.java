package com.auth0.app.model;

public class Message {
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return this.message;
    }
}

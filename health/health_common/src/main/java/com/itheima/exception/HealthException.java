package com.itheima.exception;

public class HealthException extends RuntimeException {
    private static final long serialVersionUID = 1127151684998116941L;

    public HealthException(String message) {
        super(message);
    }
}

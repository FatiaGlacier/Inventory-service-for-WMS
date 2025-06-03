package com.fatia.inventoryservice.exceptions;

public class SKUInUseException extends RuntimeException {
    public SKUInUseException(String message) {
        super(message);
    }
}

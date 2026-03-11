package dev.abhishek.ecommerce.common.exceptions;

public class InsufficientProductInventoryException extends RuntimeException {
    public InsufficientProductInventoryException(String message) {
        super(message);
    }
}

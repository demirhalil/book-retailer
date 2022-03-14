package com.book.exceptions;

public class OrderItemNumberExceedStockException extends RuntimeException {
    public OrderItemNumberExceedStockException() {
    }

    public OrderItemNumberExceedStockException(String message) {
        super(message);
    }

    public OrderItemNumberExceedStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderItemNumberExceedStockException(Throwable cause) {
        super(cause);
    }

    protected OrderItemNumberExceedStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

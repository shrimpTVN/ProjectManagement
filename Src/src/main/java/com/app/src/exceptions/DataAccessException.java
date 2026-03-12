package com.app.src.exceptions;

public class DataAccessException extends AppException {
    public DataAccessException(String message, Throwable cause) {
        super(ErrorCode.DATABASE_ERROR, message, cause);
    }
}


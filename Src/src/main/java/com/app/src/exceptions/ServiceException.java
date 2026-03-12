package com.app.src.exceptions;

public class ServiceException extends AppException {
    public ServiceException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}


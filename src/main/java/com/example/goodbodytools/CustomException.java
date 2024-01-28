package com.example.goodbodytools;

public class CustomException extends RuntimeException {
    public CustomException(String message, Throwable cause) {
        super(message, cause);
        MessageServiceHandler.addErrorMessage(super.getMessage(),"");
    }
}

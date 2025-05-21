package com.cintie.marketplace_backend.exceptions;

public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message){
        super(message);
    }
    public UnauthorizedAccessException(){
        super("Unauthorized access");
    }
}

package com.cintie.marketplace_backend.exceptions;

public class RuntimeException extends Exception{
    public RuntimeException(String message){
        super(message);
    }
    public RuntimeException(){
        super("Runtime exception");
    }
}

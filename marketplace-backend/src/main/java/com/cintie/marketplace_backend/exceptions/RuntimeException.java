package com.cintie.marketplace_backend.exceptions;

public class RuntimeException extends Exception{
    RuntimeException(String message){
        super(message);
    }
    RuntimeException(){
        super("Runtime exception");
    }
}

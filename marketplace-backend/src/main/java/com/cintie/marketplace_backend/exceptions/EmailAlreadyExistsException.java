package com.cintie.marketplace_backend.exceptions;

public class EmailAlreadyExistsException extends Exception{
    public EmailAlreadyExistsException(){
        super("Email already exsist");   
    }
    public EmailAlreadyExistsException(String message){
        super(message);   
    }
}

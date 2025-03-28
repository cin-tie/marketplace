package com.cintie.marketplace_backend.exceptions;

public class UsernameAlreadyExistsException extends Exception{
    public UsernameAlreadyExistsException(){
        super("Username already exsist");   
    }
    public UsernameAlreadyExistsException(String message){
        super(message);   
    }
}

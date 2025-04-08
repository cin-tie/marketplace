package com.cintie.marketplace_backend.exceptions;

public class TelegramAlreadyExistsException extends Exception{
    public TelegramAlreadyExistsException(){
        super("Telegram already exsist");   
    }
    public TelegramAlreadyExistsException(String message){
        super(message);   
    }
}

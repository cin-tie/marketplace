package com.cintie.marketplace_backend.exceptions;

public class BidException extends Exception{
    public BidException(){
        super("Bid exception");
    }
    public BidException(String message){
        super(message);
    }
}

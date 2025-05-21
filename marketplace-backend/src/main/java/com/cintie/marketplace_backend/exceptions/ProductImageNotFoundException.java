
package com.cintie.marketplace_backend.exceptions;

public class ProductImageNotFoundException extends Exception{
    public ProductImageNotFoundException(){
        super("Product image not found");
    }
    public ProductImageNotFoundException(String message){
        super(message);
    }
}

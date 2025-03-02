package com.shizzy.moneytransfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalArgumentException extends RuntimeException{
    public IllegalArgumentException(String message){
        super(message);
    }
}

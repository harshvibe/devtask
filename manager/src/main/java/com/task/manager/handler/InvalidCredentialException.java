package com.task.manager.handler;

public class InvalidCredentialException extends RuntimeException{

    public InvalidCredentialException(String message){
        super(message);
    }
}

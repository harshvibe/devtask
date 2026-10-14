package com.task.manager.handler;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(Long id){
        super("task not found with the id:"+id);
    }
}

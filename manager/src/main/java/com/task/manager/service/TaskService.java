package com.task.manager.service;

import com.task.manager.entity.Task;
import com.task.manager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository){
        this.taskRepository=taskRepository;
    }
    public Task create(Task task){
        return taskRepository.save(task);
    }
    public List<Task> getAllTask(){
       return taskRepository.findAll();
    }
    public Optional<Task> getTaskById(Long id){
        return  taskRepository.findById(id);
    }
    public Task updateById(Long id,Task updatedtask){
        if(id==null ||id<=0){
            throw new IllegalArgumentException("invalid task id");

        }
        Task existingtask=taskRepository.findById(id).
                orElseThrow(()->new IllegalArgumentException("task not found with this "+ id));

        existingtask.setTitle(updatedtask.getTitle());
        existingtask.setDescription(updatedtask.getDescription());
        existingtask.setCompleted(updatedtask.getCompleted());

        return taskRepository.save(existingtask);
    }
    public void deleteById(Long id){
        if(id==null ||id<=0){
            throw new IllegalArgumentException("invalid task id");

        }
        if(!taskRepository.existsById(id)){
            throw new IllegalArgumentException("task not found this "+id);
        }
         taskRepository.deleteById(id);
    }

}

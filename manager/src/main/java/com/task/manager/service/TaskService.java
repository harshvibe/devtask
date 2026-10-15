package com.task.manager.service;
import com.task.manager.dto.TaskRequestDto;
import com.task.manager.dto.TaskResponseDto;
import com.task.manager.entity.Task;
import com.task.manager.entity.User;
import com.task.manager.handler.TaskNotFoundException;
import com.task.manager.repository.TaskRepository;
import com.task.manager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,UserRepository userRepository){
        this.taskRepository=taskRepository;
        this.userRepository=userRepository;
    }

    @Transactional
    public TaskResponseDto create(Long userId, TaskRequestDto request){
        User owner=userRepository.findById(userId).
                orElseThrow(()->new InvalidCredentialException("user no longer exist"));
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setCompleted(Boolean.TRUE.equals(request.completed()));
        task.setOwner(owner);
        return TaskResponseDto.from(taskRepository.save(task));
    }
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTask(Long userId){
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
                orElseThrow(()->new TaskNotFoundException(id));

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
            throw new TaskNotFoundException(id);
        }
          taskRepository.deleteById(id);
    }

}

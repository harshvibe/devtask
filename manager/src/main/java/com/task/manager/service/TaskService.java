package com.task.manager.service;
import com.task.manager.dto.TaskRequestDto;
import com.task.manager.dto.TaskResponseDto;
import com.task.manager.entity.Task;
import com.task.manager.entity.User;
import com.task.manager.handler.InvalidCredentialException;
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

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponseDto create(Long userId, TaskRequestDto request) {
        User owner = userRepository.findById(userId).
                orElseThrow(() -> new InvalidCredentialException("user no longer exist"));
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setCompleted(Boolean.TRUE.equals(request.completed()));
        task.setOwner(owner);
        return TaskResponseDto.from(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTask(Long userId) {
        return taskRepository.findAllByOwner_Id(userId).stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long userId, Long id) {
        return TaskResponseDto.from(findOwnedTask(userId, id));
    }

    @Transactional
    public TaskResponseDto updateById(Long userId, Long id, TaskRequestDto requestDto) {
        Task task = findOwnedTask(userId, id);
        task.setTitle(requestDto.title().trim());
        task.setDescription(requestDto.description());
        if (requestDto.completed() != null) {
            task.setCompleted(requestDto.completed());
        }
        return TaskResponseDto.from(taskRepository.save(task));
    }


    @Transactional
    public void deleteById(Long userId, Long id) {
        taskRepository.delete(findOwnedTask(userId, id));
        }
    private Task findOwnedTask(Long userId, Long id) {
        return taskRepository.findByIdAndOwner_Id(id, userId)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}

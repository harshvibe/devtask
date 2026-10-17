package com.task.manager.controller;
import com.task.manager.dto.TaskRequestDto;
import com.task.manager.dto.TaskResponseDto;
import com.task.manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

   public TaskController(TaskService taskService){
       this.taskService = taskService;
    }
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TaskRequestDto requestDto){
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(taskService.create(userId(jwt),requestDto));
    }
    @GetMapping
    public List<TaskResponseDto> getAll(@AuthenticationPrincipal Jwt jwt){
       return taskService.getAllTask(userId(jwt));
    }
    @GetMapping("/{id}")
    public TaskResponseDto getById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return taskService.getTaskById(userId(jwt), id);
    }
    @PutMapping("/{id}")
    public TaskResponseDto update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id,
                               @Valid @RequestBody TaskRequestDto requestDto) {
        return taskService.updateById(userId(jwt), id, requestDto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        taskService.deleteById(userId(jwt), id);
    }
    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}


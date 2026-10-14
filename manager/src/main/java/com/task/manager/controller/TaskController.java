package com.task.manager.controller;
import com.task.manager.entity.Task;
import com.task.manager.handler.TaskNotFoundException;
import com.task.manager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;

   public TaskController(TaskService taskService){
       this.taskService = taskService;
    }
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task){
       Task created=taskService.create(task);
       return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping
    public ResponseEntity<List<Task>>  getAll(){
       List<Task> task=taskService.getAllTask();
       return ResponseEntity.ok(task);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id){
       Task task=taskService.getTaskById(id).
               orElseThrow(()->new TaskNotFoundException(id));
       return ResponseEntity.ok(task);
    }
    @PutMapping("/{id}")
    public  ResponseEntity<Task> update(@PathVariable Long id,@RequestBody Task upsatedtask){
       Task updated=taskService.updateById(id,upsatedtask);
       return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
       taskService.deleteById(id);
    }
}

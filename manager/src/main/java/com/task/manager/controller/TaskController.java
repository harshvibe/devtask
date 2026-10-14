package com.task.manager.controller;
import com.task.manager.entity.Task;
import com.task.manager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;

   public TaskController(TaskService taskService){
       this.taskService = taskService;
    }
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
       Task created=taskService.create(task);
       return ResponseEntity.ok(created);
    }
    @GetMapping
    public ResponseEntity<List<Task>>  getAll(){
       List<Task> task=taskService.getAllTask();
       return ResponseEntity.ok(task);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Task>> get(@PathVariable Long id){
       Optional<Task> get=taskService.getTaskById(id);
       return ResponseEntity.ok(get);
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

package com.task.manager.controller;
import com.task.manager.entity.Task;
import com.task.manager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    private TaskService taskService;
   public TaskController(TaskService taskService){
       this.taskService = taskService;
    }
    @PostMapping
    public ResponseEntity createTask(@RequestBody Task task){
       taskService.create(task);
       return ResponseEntity.ok("task created");
    }
    @GetMapping
    public ResponseEntity<List<Task>>  getAll(){
       List<Task> task=taskService.getAllTask();
       return ResponseEntity.ok(task);
    }
    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable Long id){
       taskService.getTaskById(id);
       return ResponseEntity.ok("this is your task");
    }
    @PutMapping("/{id}")
    public  ResponseEntity update(@PathVariable Long id,@RequestBody Task upsatedtask){
       taskService.updateById(id,upsatedtask);
       return ResponseEntity.ok("Task updated");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id){
       taskService.deleteById(id);
       return ResponseEntity.ok("Task deleted");
    }
}

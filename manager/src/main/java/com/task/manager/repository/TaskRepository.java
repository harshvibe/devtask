package com.task.manager.repository;

import com.task.manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import com.task.manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}

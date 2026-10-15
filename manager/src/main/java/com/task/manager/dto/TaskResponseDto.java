package com.task.manager.dto;

import com.task.manager.entity.Task;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        boolean completed
) {
    public static TaskResponseDto from(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCompleted()
        );
    }
}

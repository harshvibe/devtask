package com.task.manager.dto;

public record AuthResponse(
        String accessToken,
        String tokentype,
        long expireInSeconds
) {
}

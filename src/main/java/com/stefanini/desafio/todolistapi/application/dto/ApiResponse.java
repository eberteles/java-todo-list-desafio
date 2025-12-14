package com.stefanini.desafio.todolistapi.application.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        String message,
        T data,
        LocalDateTime timestamp
) {
    public ApiResponse(String message, T data) {
        this(message, data, LocalDateTime.now());
    }
    
    public ApiResponse(String message) {
        this(message, null, LocalDateTime.now());
    }
}

package com.stefanini.desafio.todolistapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record Task(
        UUID id,
        String title,
        String description,
        LocalDateTime creationDate,
        TaskStatus status
) {
    public Task(String title, String description) {
        this(
                UUID.randomUUID(),
                title,
                description,
                LocalDateTime.now(),
                TaskStatus.PENDENTE
        );
    }
}

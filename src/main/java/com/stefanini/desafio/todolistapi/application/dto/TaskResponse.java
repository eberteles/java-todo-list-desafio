package com.stefanini.desafio.todolistapi.application.dto;

import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(

        UUID id,
        String title,
        String description,
        LocalDateTime creationDate,
        LocalDateTime updateDate,
        TaskStatus status

) {}
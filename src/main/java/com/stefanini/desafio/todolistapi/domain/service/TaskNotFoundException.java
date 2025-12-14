package com.stefanini.desafio.todolistapi.domain.service;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
public class TaskNotFoundException extends RuntimeException {

    private final UUID taskId;

    public TaskNotFoundException(UUID id) {
        super();
        this.taskId = id;
    }
}

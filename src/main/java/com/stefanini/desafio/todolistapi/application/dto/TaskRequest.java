package com.stefanini.desafio.todolistapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(

        @NotBlank(message = "{task.title.not.empty}")
        @Size(min = 1, max = 100, message = "{task.title.not.empty}")
        String title,

        @Size(max = 500, message = "{task.description.size}")
        String description

) {}
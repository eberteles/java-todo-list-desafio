package com.stefanini.desafio.todolistapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateRequest(
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
        String title,

        @Size(max = 4000, message = "A descrição é muito longa.")
        String description
) {}

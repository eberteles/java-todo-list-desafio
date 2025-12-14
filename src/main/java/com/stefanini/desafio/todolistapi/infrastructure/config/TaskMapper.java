package com.stefanini.desafio.todolistapi.infrastructure.config;

import com.stefanini.desafio.todolistapi.application.dto.TaskRequest;
import com.stefanini.desafio.todolistapi.application.dto.TaskResponse;
import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    // 1. DTO de Criação -> Entidade de Domínio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updateDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(com.stefanini.desafio.todolistapi.domain.model.TaskStatus.PENDENTE)")
    Task toDomain(TaskRequest dto);

    // 2. Entidade de Domínio -> DTO de Resposta
    TaskResponse toResponse(Task domain);

    // 3. Entidade de Domínio -> Entidade de Persistência
    TaskEntity toEntity(Task domain);

    // 4. Entidade de Persistência -> Entidade de Domínio
    Task toDomain(TaskEntity entity);

}

package com.stefanini.desafio.todolistapi.infrastructure.config;

import com.stefanini.desafio.todolistapi.application.dto.TaskCreateRequest;
import com.stefanini.desafio.todolistapi.application.dto.TaskResponse;
import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    // 1. DTO de Criação -> Entidade de Domínio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(com.stefanini.desafio.todolistapi.domain.model.TaskStatus.PENDENTE)")
    Task toDomain(TaskCreateRequest dto);

    // 2. Entidade de Domínio -> DTO de Resposta
    TaskResponse toResponse(Task domain);

    // 3. Entidade de Domínio -> Entidade de Persistência
    TaskEntity toEntity(Task domain);

    // 4. Entidade de Persistência -> Entidade de Domínio (A CORREÇÃO PRINCIPAL)
    // Mapeamento explícito de campos para o construtor do Record 'Task'
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "title", source = "entity.title")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "creationDate", source = "entity.creationDate")
    @Mapping(target = "status", source = "entity.status")
    Task toDomain(TaskEntity entity);

}

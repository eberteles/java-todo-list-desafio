package com.stefanini.desafio.todolistapi.infrastructure.config;

import com.stefanini.desafio.todolistapi.application.dto.TaskRequest;
import com.stefanini.desafio.todolistapi.application.dto.TaskResponse;
import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private final TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    @DisplayName("Deve converter TaskRequest para Domain corretamente")
    void toDomain_FromRequest() {
        TaskRequest request = new TaskRequest("Título Teste", "Descrição Teste");

        Task domain = mapper.toDomain(request);

        assertNotNull(domain);
        assertNull(domain.id()); // ID deve ser nulo na criação
        assertEquals("Título Teste", domain.title());
        assertEquals("Descrição Teste", domain.description());
        assertEquals(TaskStatus.PENDENTE, domain.status()); // Deve ter o valor default
        assertNotNull(domain.creationDate()); // Deve ser gerado automaticamente
        assertNotNull(domain.updateDate()); // Deve ser gerado automaticamente
    }

    @Test
    @DisplayName("Deve converter Domain para TaskResponse corretamente")
    void toResponse() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Task domain = new Task(id, "Título", "Desc", now, now, TaskStatus.CONCLUIDA);

        TaskResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Título", response.title());
        assertEquals("Desc", response.description());
        assertEquals(now, response.creationDate());
        assertEquals(now, response.updateDate());
        assertEquals(TaskStatus.CONCLUIDA, response.status());
    }

    @Test
    @DisplayName("Deve converter TaskEntity para Domain corretamente")
    void toDomain_FromEntity() {

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        TaskEntity entity = new TaskEntity(id, "Título Entity", "Desc Entity", now, now, TaskStatus.PENDENTE);

        Task domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(id, domain.id());
        assertEquals("Título Entity", domain.title());
        assertEquals("Desc Entity", domain.description());
        assertEquals(now, domain.creationDate());
        assertEquals(now, domain.updateDate());
        assertEquals(TaskStatus.PENDENTE, domain.status());
    }

    @Test
    @DisplayName("Deve converter Domain para TaskEntity corretamente")
    void toEntity() {

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Task domain = new Task(id, "Título Domain", "Desc Domain", now, now, TaskStatus.CONCLUIDA);

        TaskEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Título Domain", entity.getTitle());
        assertEquals("Desc Domain", entity.getDescription());
        assertEquals(now, entity.getCreationDate());
        assertEquals(now, entity.getUpdateDate());
        assertEquals(TaskStatus.CONCLUIDA, entity.getStatus());
    }
}

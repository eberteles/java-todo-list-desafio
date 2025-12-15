package com.stefanini.desafio.todolistapi.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanini.desafio.todolistapi.application.dto.TaskRequest;
import com.stefanini.desafio.todolistapi.application.dto.TaskResponse;
import com.stefanini.desafio.todolistapi.application.exception.CustomAuthenticationEntryPoint;
import com.stefanini.desafio.todolistapi.application.exception.CustomExceptionHandler;
import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.domain.service.TaskNotFoundException;
import com.stefanini.desafio.todolistapi.domain.service.TaskService;
import com.stefanini.desafio.todolistapi.infrastructure.config.SecurityConfig;
import com.stefanini.desafio.todolistapi.infrastructure.config.TaskMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomExceptionHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    @DisplayName("Deve criar tarefa com sucesso (201 Created)")
    @WithMockUser(username = "stefuser", roles = "USER")
    void create_Success() throws Exception {
        // Arrange
        TaskRequest request = new TaskRequest("Comprar café", "Extra forte");
        Task domainTask = new Task(UUID.randomUUID(), "Comprar café", "Extra forte", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskResponse response = new TaskResponse(domainTask.id(), domainTask.title(), domainTask.description(), domainTask.creationDate(), domainTask.updateDate(), domainTask.status());

        when(taskMapper.toDomain(any(TaskRequest.class))).thenReturn(domainTask);
        when(taskService.createTask(any(Task.class))).thenReturn(domainTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.title").value("Comprar café"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar criar tarefa sem título")
    @WithMockUser(username = "stefuser", roles = "USER")
    void create_Invalid() throws Exception {
        // Arrange
        TaskRequest request = new TaskRequest("", "Descrição válida"); // Título vazio

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation Failed"));
    }

    @Test
    @DisplayName("Deve listar todas as tarefas (200 OK)")
    @WithMockUser(username = "stefuser", roles = "USER")
    void findAll_Success() throws Exception {
        // Arrange
        Task domainTask = new Task(UUID.randomUUID(), "Tarefa 1", "Desc 1", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskResponse response = new TaskResponse(domainTask.id(), domainTask.title(), domainTask.description(), domainTask.creationDate(), domainTask.updateDate(), domainTask.status());

        when(taskService.findAllTasks()).thenReturn(List.of(domainTask));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tarefa 1"));
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso (200 OK)")
    @WithMockUser(username = "stefuser", roles = "USER")
    void findById_Success() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Task domainTask = new Task(id, "Tarefa Busca", "Desc Busca", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskResponse response = new TaskResponse(id, domainTask.title(), domainTask.description(), domainTask.creationDate(), domainTask.updateDate(), domainTask.status());

        when(taskService.findTaskById(id)).thenReturn(domainTask);
        when(taskMapper.toResponse(domainTask)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Busca"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    @WithMockUser(username = "stefuser", roles = "USER")
    void findById_NotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(taskService.findTaskById(id)).thenThrow(new TaskNotFoundException(id));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso (200 OK)")
    @WithMockUser(username = "stefuser", roles = "USER")
    void update_Success() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TaskRequest request = new TaskRequest("Título Atualizado", "Desc Atualizada");
        Task updatedTask = new Task(id, "Título Atualizado", "Desc Atualizada", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.CONCLUIDA);
        TaskResponse response = new TaskResponse(id, updatedTask.title(), updatedTask.description(), updatedTask.creationDate(), updatedTask.updateDate(), updatedTask.status());

        when(taskService.updateTask(eq(id), eq(request.title()), eq(request.description()), any())).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.title").value("Título Atualizado"));
    }

    @Test
    @DisplayName("Deve excluir tarefa com sucesso (200 OK)")
    @WithMockUser(username = "stefuser", roles = "USER")
    void delete_Success() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar excluir tarefa inexistente")
    @WithMockUser(username = "stefuser", roles = "USER")
    void delete_NotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new TaskNotFoundException(id)).when(taskService).deleteTask(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    
    @Test
    @DisplayName("Deve retornar 401 Unauthorized quando não autenticado")
    void create_Unauthorized() throws Exception {
        // Arrange
        TaskRequest request = new TaskRequest("Teste", "Teste");

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

package com.stefanini.desafio.todolistapi.application.controller;

import com.stefanini.desafio.todolistapi.application.dto.ApiResponse;
import com.stefanini.desafio.todolistapi.application.dto.TaskRequest;
import com.stefanini.desafio.todolistapi.application.dto.TaskResponse;
import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.domain.service.TaskService;
import com.stefanini.desafio.todolistapi.infrastructure.config.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@SecurityRequirement(name = "BasicAuth")
@Tag(name = "Tasks", description = "Endpoints para Gerenciamento de Tarefas")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final MessageSource messageSource;

    public TaskController(TaskService taskService, TaskMapper taskMapper, MessageSource messageSource) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.messageSource = messageSource;
    }

    /**
     * Cria uma nova tarefa.
     * @param request O corpo da requisição contendo os detalhes da tarefa.
     * @return Uma resposta padronizada com a mensagem de sucesso e os dados da nova tarefa.
     */
    @PostMapping
    @Operation(summary = "Criar uma nova tarefa", description = "Cria uma nova tarefa com título e descrição.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody TaskRequest request) {
        Task domainTask = taskService.createTask(taskMapper.toDomain(request));
        TaskResponse taskResponse = taskMapper.toResponse(domainTask);

        String message = messageSource.getMessage("task.created.success", new Object[]{domainTask.title()}, LocaleContextHolder.getLocale());
        ApiResponse<TaskResponse> response = new ApiResponse<>(message, taskResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as tarefas cadastradas.
     * @return Uma lista de todas as tarefas.
     */
    @GetMapping
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista com todas as tarefas cadastradas.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    public List<TaskResponse> findAll() {
        return taskService.findAllTasks().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma tarefa específica pelo seu ID.
     * @param id O ID da tarefa a ser buscada.
     * @return A tarefa encontrada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna uma tarefa específica com base no seu ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public TaskResponse findById(@PathVariable UUID id) {
        return taskMapper.toResponse(taskService.findTaskById(id));
    }

    /**
     * Atualiza uma tarefa existente.
     * @param id O ID da tarefa a ser atualizada.
     * @param request O corpo da requisição com os novos dados da tarefa.
     * @param status O novo status da tarefa (opcional).
     * @return Uma resposta padronizada com a mensagem de sucesso e os dados da tarefa atualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma tarefa", description = "Atualiza o título, a descrição e/ou o status de uma tarefa existente.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ApiResponse<TaskResponse> update(
            @PathVariable UUID id,
            @RequestBody TaskRequest request,
            @RequestParam(required = false) TaskStatus status
    ) {
        Task updatedTask = taskService.updateTask(
                id,
                request.title(),
                request.description(),
                status
        );
        TaskResponse taskResponse = taskMapper.toResponse(updatedTask);
        
        String message = messageSource.getMessage("task.updated.success", new Object[]{id}, LocaleContextHolder.getLocale());
        return new ApiResponse<>(message, taskResponse);
    }

    /**
     * Exclui uma tarefa pelo seu ID.
     * @param id O ID da tarefa a ser excluída.
     * @return Uma resposta padronizada com a mensagem de sucesso.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma tarefa", description = "Exclui uma tarefa permanentemente com base no seu ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tarefa excluída com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        taskService.deleteTask(id);
        String message = messageSource.getMessage("task.deleted.success", new Object[]{id}, LocaleContextHolder.getLocale());
        return new ApiResponse<>(message);
    }
}

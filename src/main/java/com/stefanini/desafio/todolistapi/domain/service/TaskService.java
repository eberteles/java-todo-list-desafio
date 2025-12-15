package com.stefanini.desafio.todolistapi.domain.service;

import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.infrastructure.config.TaskMapper;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Camada de serviço para a lógica de negócio relacionada a tarefas.
 * Todas as operações são transacionais.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Cria uma nova tarefa no banco de dados.
     * @param task O objeto de domínio da tarefa a ser criada.
     * @return A tarefa criada, com ID e datas preenchidas.
     */
    public Task createTask(Task task) {
        TaskEntity entity = taskMapper.toEntity(task);
        entity = taskRepository.save(entity);
        return taskMapper.toDomain(entity);
    }

    /**
     * Retorna uma lista com todas as tarefas cadastradas.
     * @return Uma lista de objetos de domínio Task.
     */
    public List<Task> findAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma tarefa específica pelo seu ID.
     * @param id O UUID da tarefa a ser encontrada.
     * @return O objeto de domínio da tarefa correspondente.
     * @throws TaskNotFoundException se nenhuma tarefa for encontrada com o ID fornecido.
     */
    public Task findTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(taskMapper::toDomain)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Atualiza os dados de uma tarefa existente.
     * A data de atualização é sempre renovada.
     * @param id O UUID da tarefa a ser atualizada.
     * @param title O novo título da tarefa (se nulo, mantém o atual).
     * @param description A nova descrição da tarefa (se nula, mantém a atual).
     * @param status O novo status da tarefa (se nulo, mantém o atual).
     * @return O objeto de domínio da tarefa com os dados atualizados.
     * @throws TaskNotFoundException se nenhuma tarefa for encontrada com o ID fornecido.
     */
    public Task updateTask(UUID id, String title, String description, TaskStatus status) {
        Task existingTask = findTaskById(id);

        Task updatedTask = new Task(
                existingTask.id(),
                title != null ? title : existingTask.title(),
                description != null ? description : existingTask.description(),
                existingTask.creationDate(),
                LocalDateTime.now(),
                status != null ? status : existingTask.status()
        );

        TaskEntity entity = taskMapper.toEntity(updatedTask);
        taskRepository.save(entity);

        return updatedTask;
    }

    /**
     * Exclui uma tarefa do banco de dados com base no seu ID.
     * @param id O UUID da tarefa a ser excluída.
     * @throws TaskNotFoundException se nenhuma tarefa for encontrada com o ID fornecido.
     */
    public void deleteTask(UUID id) {
        Task taskToDelete = findTaskById(id);
        taskRepository.deleteById(taskToDelete.id());
    }

}

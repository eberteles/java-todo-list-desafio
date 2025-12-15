package com.stefanini.desafio.todolistapi.infrastructure.persistence.repository;

import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    /**
     * Encontra todas as tarefas que correspondem a um determinado status.
     * @param status O status da tarefa a ser buscado (ex: PENDENTE, CONCLUIDA).
     * @return Uma lista de entidades de tarefa com o status especificado.
     */
    List<TaskEntity> findByStatus(TaskStatus status);
}

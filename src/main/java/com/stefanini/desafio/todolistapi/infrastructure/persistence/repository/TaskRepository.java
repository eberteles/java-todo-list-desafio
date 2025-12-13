package com.stefanini.desafio.todolistapi.infrastructure.persistence.repository;

import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
}

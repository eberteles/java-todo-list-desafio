package com.stefanini.desafio.todolistapi.infrastructure.persistence.repository;

import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Deve salvar uma tarefa com sucesso")
    void save_Success() {
        // Arrange
        TaskEntity task = new TaskEntity(null, "Tarefa Teste", "Descrição Teste", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);

        // Act
        TaskEntity savedTask = taskRepository.save(task);

        // Assert
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Tarefa Teste");
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void findById_Success() {
        // Arrange
        TaskEntity task = new TaskEntity(null, "Buscar Teste", "Desc", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskEntity persistedTask = entityManager.persistFlushFind(task);

        // Act
        Optional<TaskEntity> foundTask = taskRepository.findById(persistedTask.getId());

        // Assert
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Buscar Teste");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar ID inexistente")
    void findById_NotFound() {
        // Act
        Optional<TaskEntity> foundTask = taskRepository.findById(UUID.randomUUID());

        // Assert
        assertThat(foundTask).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar apenas tarefas com status PENDENTE")
    void findByStatus_shouldReturnOnlyPendingTasks() {
        // Arrange
        TaskEntity pendingTask1 = new TaskEntity(null, "Tarefa Pendente 1", "Desc", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskEntity completedTask = new TaskEntity(null, "Tarefa Concluída", "Desc", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.CONCLUIDA);
        TaskEntity pendingTask2 = new TaskEntity(null, "Tarefa Pendente 2", "Desc", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);

        entityManager.persist(pendingTask1);
        entityManager.persist(completedTask);
        entityManager.persist(pendingTask2);
        entityManager.flush();

        // Act
        List<TaskEntity> foundTasks = taskRepository.findByStatus(TaskStatus.PENDENTE);

        // Assert
        assertThat(foundTasks).hasSize(2);
        assertThat(foundTasks).extracting(TaskEntity::getStatus).containsOnly(TaskStatus.PENDENTE);
        assertThat(foundTasks).extracting(TaskEntity::getTitle).containsExactlyInAnyOrder("Tarefa Pendente 1", "Tarefa Pendente 2");
    }

    @Test
    @DisplayName("Deve excluir uma tarefa com sucesso")
    void deleteById_Success() {
        // Arrange
        TaskEntity task = new TaskEntity(null, "Deletar Teste", "Desc", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        TaskEntity persistedTask = entityManager.persistFlushFind(task);

        // Act
        taskRepository.deleteById(persistedTask.getId());

        // Assert
        TaskEntity deletedTask = entityManager.find(TaskEntity.class, persistedTask.getId());
        assertThat(deletedTask).isNull();
    }
}

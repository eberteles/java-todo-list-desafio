package com.stefanini.desafio.todolistapi.domain.service;

import com.stefanini.desafio.todolistapi.domain.model.Task;
import com.stefanini.desafio.todolistapi.domain.model.TaskStatus;
import com.stefanini.desafio.todolistapi.infrastructure.config.TaskMapper;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.entity.TaskEntity;
import com.stefanini.desafio.todolistapi.infrastructure.persistence.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Deve criar uma tarefa com sucesso")
    void createTask_Success() {
        // Arrange
        Task taskInput = new Task(null, "Comprar café", "Comprar pó de café extra forte", null, null, TaskStatus.PENDENTE);
        TaskEntity entityInput = new TaskEntity();
        TaskEntity entitySaved = new TaskEntity(UUID.randomUUID(), "Comprar café", "Comprar pó de café extra forte", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        Task taskOutput = new Task(entitySaved.getId(), "Comprar café", "Comprar pó de café extra forte", entitySaved.getCreationDate(), entitySaved.getUpdateDate(), TaskStatus.PENDENTE);

        when(taskMapper.toEntity(taskInput)).thenReturn(entityInput);
        when(taskRepository.save(entityInput)).thenReturn(entitySaved);
        when(taskMapper.toDomain(entitySaved)).thenReturn(taskOutput);

        // Act
        Task result = taskService.createTask(taskInput);

        // Assert
        assertNotNull(result);
        assertEquals(taskOutput.id(), result.id());
        assertEquals("Comprar café", result.title());
        verify(taskRepository, times(1)).save(entityInput);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void findAllTasks_Success() {
        // Arrange
        TaskEntity entity = new TaskEntity(UUID.randomUUID(), "Estudar Spring Boot", "Revisar conceitos de JPA", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        Task task = new Task(entity.getId(), "Estudar Spring Boot", "Revisar conceitos de JPA", entity.getCreationDate(), entity.getUpdateDate(), TaskStatus.PENDENTE);

        when(taskRepository.findAll()).thenReturn(List.of(entity));
        when(taskMapper.toDomain(entity)).thenReturn(task);

        // Act
        List<Task> result = taskService.findAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.id(), result.getFirst().id());
        assertEquals("Estudar Spring Boot", result.getFirst().title());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void findTaskById_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        TaskEntity entity = new TaskEntity(id, "Pagar contas", "Pagar conta de luz e internet", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        Task task = new Task(id, "Pagar contas", "Pagar conta de luz e internet", entity.getCreationDate(), entity.getUpdateDate(), TaskStatus.PENDENTE);

        when(taskRepository.findById(id)).thenReturn(Optional.of(entity));
        when(taskMapper.toDomain(entity)).thenReturn(task);

        // Act
        Task result = taskService.findTaskById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Pagar contas", result.title());
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void findTaskById_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(id));
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void updateTask_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        TaskEntity existingEntity = new TaskEntity(id, "Ler livro", "Ler 10 páginas", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        Task existingTask = new Task(id, "Ler livro", "Ler 10 páginas", existingEntity.getCreationDate(), existingEntity.getUpdateDate(), TaskStatus.PENDENTE);

        // Mock do findById (chamado internamente pelo updateTask)
        when(taskRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(taskMapper.toDomain(existingEntity)).thenReturn(existingTask);

        // Mock do save
        when(taskMapper.toEntity(any(Task.class))).thenReturn(existingEntity); 
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(existingEntity);

        // Act
        Task result = taskService.updateTask(id, "Ler livro técnico", "Ler capítulo sobre Testes", TaskStatus.CONCLUIDA);

        // Assert
        assertNotNull(result);
        assertEquals("Ler livro técnico", result.title());
        assertEquals("Ler capítulo sobre Testes", result.description());
        assertEquals(TaskStatus.CONCLUIDA, result.status());
        
        // Verifica se o save foi chamado
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar tarefa inexistente")
    void updateTask_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> 
            taskService.updateTask(id, "Título Novo", "Descrição Nova", TaskStatus.CONCLUIDA)
        );
        
        // Garante que o save NUNCA foi chamado
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir tarefa com sucesso")
    void deleteTask_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        TaskEntity existingEntity = new TaskEntity(id, "Fazer exercícios", "Ir para a academia", LocalDateTime.now(), LocalDateTime.now(), TaskStatus.PENDENTE);
        Task existingTask = new Task(id, "Fazer exercícios", "Ir para a academia", existingEntity.getCreationDate(), existingEntity.getUpdateDate(), TaskStatus.PENDENTE);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(taskMapper.toDomain(existingEntity)).thenReturn(existingTask);

        // Act
        taskService.deleteTask(id);

        // Assert
        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir tarefa inexistente")
    void deleteTask_NotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(id));
        verify(taskRepository, never()).deleteById(any());
    }
}

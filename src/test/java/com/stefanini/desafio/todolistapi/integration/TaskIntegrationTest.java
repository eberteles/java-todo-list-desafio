package com.stefanini.desafio.todolistapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefanini.desafio.todolistapi.application.dto.TaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }
    }

    @Test
    @DisplayName("Fluxo completo: Criar, Listar, Atualizar e Deletar Tarefa")
    @WithMockUser(username = "stefuser", roles = "USER")
    void fullTaskLifecycle() throws Exception {
        
        // 1. CRIAR TAREFA (POST)
        TaskRequest createRequest = new TaskRequest("Tarefa Integração", "Testando fluxo completo");
        
        MvcResult createResult = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Tarefa Integração"))
                .andExpect(jsonPath("$.data.status").value("PENDENTE"))
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();

        // Extrair o ID da tarefa criada da resposta
        String responseBody = createResult.getResponse().getContentAsString();
        String idString = objectMapper.readTree(responseBody).path("data").path("id").asText();
        UUID taskId = UUID.fromString(idString);

        // 2. BUSCAR POR ID (GET)
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Integração"));

        // 3. ATUALIZAR TAREFA (PUT)
        TaskRequest updateRequest = new TaskRequest("Tarefa Atualizada", "Descrição Atualizada");
        
        mockMvc.perform(put("/api/v1/tasks/{id}", taskId)
                        .param("status", "CONCLUIDA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Tarefa Atualizada"))
                .andExpect(jsonPath("$.data.status").value("CONCLUIDA"));

        // 4. LISTAR TODAS (GET) - Deve conter a tarefa atualizada
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id == '" + taskId + "')].title").value("Tarefa Atualizada"));

        // 5. DELETAR TAREFA (DELETE)
        mockMvc.perform(delete("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 6. VERIFICAR EXCLUSÃO (GET)
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }
}

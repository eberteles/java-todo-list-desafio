-- V1__create_tasks_table.sql (Flyway Script para SQL Server)

CREATE TABLE tasks (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(MAX),
    creation_date DATETIME2 NOT NULL,
    status VARCHAR(50) NOT NULL
);
GO

CREATE INDEX idx_task_status ON tasks (status);
GO
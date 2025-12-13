IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = 'ToDoListDB'
)
BEGIN
    CREATE DATABASE ToDoListDB;
END
GO

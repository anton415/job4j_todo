--liquibase formatted sql

--changeset job4j:002
CREATE TABLE todo_user (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

--rollback DROP TABLE IF EXISTS todo_user;

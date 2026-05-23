--liquibase formatted sql

--changeset job4j:001
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created TIMESTAMP,
    done BOOLEAN
);

--rollback DROP TABLE IF EXISTS tasks;

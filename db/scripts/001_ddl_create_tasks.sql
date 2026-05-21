--liquibase formatted sql

--changeset job4j:001
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    description TEXT,
    created TIMESTAMP,
    done BOOLEAN
);

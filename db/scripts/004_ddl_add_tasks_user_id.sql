--liquibase formatted sql

--changeset job4j:004
ALTER TABLE tasks ADD COLUMN user_id INT;

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_user
    FOREIGN KEY (user_id)
    REFERENCES todo_user(id);

--rollback ALTER TABLE tasks DROP CONSTRAINT IF EXISTS fk_tasks_user;
--rollback ALTER TABLE tasks DROP COLUMN IF EXISTS user_id;

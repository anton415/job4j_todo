--liquibase formatted sql

--changeset job4j:006
ALTER TABLE todo_user ADD COLUMN user_zone VARCHAR(255);

--rollback ALTER TABLE todo_user DROP COLUMN IF EXISTS user_zone;

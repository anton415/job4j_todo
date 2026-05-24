--liquibase formatted sql

--changeset job4j:003
CREATE UNIQUE INDEX todo_user_lower_login_unique_idx
    ON todo_user (LOWER(login));

--rollback DROP INDEX IF EXISTS todo_user_lower_login_unique_idx;

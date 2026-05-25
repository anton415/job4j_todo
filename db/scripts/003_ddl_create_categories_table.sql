--liquibase formatted sql

--changeset job4j:003-create-categories
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tasks_categories (
    task_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (task_id, category_id),
    CONSTRAINT fk_tasks_categories_task
        FOREIGN KEY (task_id)
        REFERENCES tasks(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tasks_categories_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

--rollback DROP TABLE IF EXISTS tasks_categories;
--rollback DROP TABLE IF EXISTS categories;

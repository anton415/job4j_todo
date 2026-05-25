--liquibase formatted sql

--changeset job4j:005
CREATE TABLE priorities (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    position INT
);

INSERT INTO priorities (name, position) VALUES ('urgently', 1);
INSERT INTO priorities (name, position) VALUES ('normal', 2);

ALTER TABLE tasks ADD COLUMN priority_id INT;

UPDATE tasks
SET priority_id = (
    SELECT id
    FROM priorities
    WHERE name = 'normal'
);

ALTER TABLE tasks ALTER COLUMN priority_id SET NOT NULL;

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_priority
    FOREIGN KEY (priority_id)
    REFERENCES priorities(id);

--rollback ALTER TABLE tasks DROP CONSTRAINT IF EXISTS fk_tasks_priority;
--rollback ALTER TABLE tasks DROP COLUMN IF EXISTS priority_id;
--rollback DROP TABLE IF EXISTS priorities;

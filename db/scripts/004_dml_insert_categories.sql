--liquibase formatted sql

--changeset job4j:004-insert-categories
INSERT INTO categories (name) VALUES ('work');
INSERT INTO categories (name) VALUES ('study');
INSERT INTO categories (name) VALUES ('home');
INSERT INTO categories (name) VALUES ('shopping');

--rollback DELETE FROM categories WHERE name IN ('work', 'study', 'home', 'shopping');

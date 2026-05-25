package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskStore {

    private final CrudRepository crudRepository;

    public List<Task> findAll() {
        return crudRepository.query("""
                SELECT task
                FROM Task task
                LEFT JOIN FETCH task.user
                LEFT JOIN FETCH task.priority
                ORDER BY task.created DESC
                """, Task.class);
    }

    public List<Task> findByDone(boolean done) {
        return crudRepository.query(
                """
                        SELECT task
                        FROM Task task
                        LEFT JOIN FETCH task.user
                        LEFT JOIN FETCH task.priority
                        WHERE task.done = :done
                        ORDER BY task.created DESC
                        """,
                Task.class,
                Map.of("done", done)
        );
    }

    public Optional<Task> findById(int id) {
        return crudRepository.optional(
                """
                        SELECT task
                        FROM Task task
                        LEFT JOIN FETCH task.user
                        LEFT JOIN FETCH task.priority
                        WHERE task.id = :id
                        """,
                Task.class,
                Map.of("id", id)
        );
    }

    public Task save(Task task) {
        crudRepository.run(session -> session.persist(task));
        return task;
    }

    public boolean update(Task task) {
        return crudRepository.tx(session -> session
                .createQuery("""
                        UPDATE Task
                        SET title = :title,
                            description = :description,
                            done = :done
                        WHERE id = :id
                        """)
                .setParameter("title", task.getTitle())
                .setParameter("description", task.getDescription())
                .setParameter("done", task.isDone())
                .setParameter("id", task.getId())
                .executeUpdate() > 0);
    }

    public boolean setDone(int id) {
        return crudRepository.tx(session -> session
                .createQuery("""
                        UPDATE Task
                        SET done = TRUE
                        WHERE id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }

    public boolean delete(int id) {
        return crudRepository.tx(session -> session
                .createQuery("""
                        DELETE FROM Task
                        WHERE id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }
}

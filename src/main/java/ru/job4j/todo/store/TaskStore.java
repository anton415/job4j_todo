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
        return crudRepository.query("from Task order by created desc", Task.class);
    }

    public List<Task> findByDone(boolean done) {
        return crudRepository.query(
                "from Task where done = :done order by created desc",
                Task.class,
                Map.of("done", done)
        );
    }

    public Optional<Task> findById(int id) {
        return crudRepository.tx(session -> Optional.ofNullable(session.get(Task.class, id)));
    }

    public Task save(Task task) {
        crudRepository.run(session -> session.persist(task));
        return task;
    }

    public boolean update(Task task) {
        return crudRepository.tx(session -> session
                .createQuery("""
                        update Task
                        set title = :title,
                            description = :description,
                            done = :done
                        where id = :id
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
                        update Task
                        set done = true
                        where id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }

    public boolean delete(int id) {
        return crudRepository.tx(session -> session
                .createQuery("""
                        delete from Task
                        where id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }
}

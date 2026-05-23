package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class TaskStore {

    private final SessionFactory sf;

    public List<Task> findAll() {
        return tx(session -> session
                .createQuery("from Task order by created desc", Task.class)
                .list());
    }

    public List<Task> findByDone(boolean done) {
        return tx(session -> session
                .createQuery("from Task where done = :done order by created desc", Task.class)
                .setParameter("done", done)
                .list());
    }

    public Optional<Task> findById(int id) {
        return tx(session -> Optional.ofNullable(session.get(Task.class, id)));
    }

    public Task save(Task task) {
        tx(session -> {
            session.save(task);
            return task;
        });
        return task;
    }

    public boolean update(Task task) {
        return tx(session -> session
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
        return tx(session -> session
                .createQuery("""
                        update Task
                        set done = true
                        where id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }

    public boolean delete(int id) {
        return tx(session -> session
                .createQuery("""
                        delete from Task
                        where id = :id
                        """)
                .setParameter("id", id)
                .executeUpdate() > 0);
    }

    private <T> T tx(Function<Session, T> command) {
        Transaction transaction = null;
        try (Session session = sf.openSession()) {
            transaction = session.beginTransaction();
            T result = command.apply(session);
            transaction.commit();
            return result;
        } catch (Exception exception) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exception;
        }
    }
}

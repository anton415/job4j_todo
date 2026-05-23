package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class UserStore {

    private final SessionFactory sf;

    public Optional<User> findByLogin(String login) {
        return tx(session -> session
                .createQuery("from TodoUser where login = :login", User.class)
                .setParameter("login", login)
                .list()
                .stream()
                .findFirst());
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return tx(session -> session
                .createQuery("""
                        from TodoUser
                        where login = :login
                          and password = :password
                        """, User.class)
                .setParameter("login", login)
                .setParameter("password", password)
                .list()
                .stream()
                .findFirst());
    }

    public User save(User user) {
        tx(session -> {
            session.save(user);
            return user;
        });
        return user;
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

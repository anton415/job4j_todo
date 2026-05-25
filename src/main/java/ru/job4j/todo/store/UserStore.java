package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStore {

    private final CrudRepository crudRepository;

    public Optional<User> findByLogin(String login) {
        return crudRepository.optional(
                "FROM TodoUser WHERE LOWER(login) = :login",
                User.class,
                Map.of("login", login)
        );
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                """
                        FROM TodoUser
                        WHERE LOWER(login) = :login
                          AND password = :password
                        """,
                User.class,
                Map.of("login", login, "password", password)
        );
    }

    public User save(User user) {
        crudRepository.run(session -> session.persist(user));
        return user;
    }
}

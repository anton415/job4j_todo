package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.UserStore;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStore userStore;

    public Optional<User> create(String name, String login, String password) {
        String normalizedName = normalize(name);
        String normalizedLogin = normalize(login);
        if (isBlank(normalizedName)
                || isBlank(normalizedLogin)
                || isBlank(password)
                || userStore.findByLogin(normalizedLogin).isPresent()) {
            return Optional.empty();
        }
        var user = User.builder()
                .name(normalizedName)
                .login(normalizedLogin)
                .password(password)
                .build();
        return Optional.of(userStore.save(user));
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        if (isBlank(login) || isBlank(password)) {
            return Optional.empty();
        }
        return userStore.findByLoginAndPassword(login.trim(), password);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

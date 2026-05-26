package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.UserStore;

import javax.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStore userStore;
    private final Validator validator;

    public Optional<User> create(String name, String login, String password) {
        return create(name, login, password, null);
    }

    public Optional<User> create(String name, String login, String password, String timezone) {
        if (!TimeZoneHelper.isSupported(timezone)) {
            return Optional.empty();
        }
        var user = User.builder()
                .name(normalize(name))
                .login(normalizeLogin(login))
                .password(password)
                .timezone(TimeZoneHelper.normalize(timezone))
                .build();
        if (!validator.validate(user).isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(userStore.save(user));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        String normalizedLogin = normalizeLogin(login);
        if (hasViolations("login", normalizedLogin)
                || hasViolations("password", password)) {
            return Optional.empty();
        }
        return userStore.findByLoginAndPassword(normalizedLogin, password);
    }

    public List<TimeZone> findAllTimeZones() {
        return TimeZoneHelper.findAll();
    }

    public String findDefaultTimeZoneId() {
        return TimeZoneHelper.defaultTimeZoneId();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeLogin(String login) {
        String normalizedLogin = normalize(login);
        return normalizedLogin == null ? null : normalizedLogin.toLowerCase(Locale.ROOT);
    }

    private boolean hasViolations(String propertyName, String value) {
        return !validator.validateValue(User.class, propertyName, value).isEmpty();
    }
}

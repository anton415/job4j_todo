package ru.job4j.todo.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.UserStore;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static ValidatorFactory validatorFactory;
    private UserStore userStore;
    private UserService userService;

    @BeforeAll
    static void initValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @BeforeEach
    void initService() {
        userStore = mock(UserStore.class);
        userService = new UserService(userStore, validatorFactory.getValidator());
    }

    @Test
    void whenCreateWithMixedCaseLoginThenSaveNormalizedLogin() {
        when(userStore.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> user = userService.create(" Anton ", " User@mail.ru ", "password");

        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Anton");
        assertThat(user.get().getLogin()).isEqualTo("user@mail.ru");
        verify(userStore).save(any(User.class));
        verify(userStore, never()).findByLogin(anyString());
    }

    @Test
    void whenCreateWithTimezoneThenSaveNormalizedTimezone() {
        when(userStore.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> user = userService.create("Anton", "user@mail.ru", "password", " Europe/Moscow ");

        assertThat(user).isPresent();
        assertThat(user.get().getTimezone()).isEqualTo("Europe/Moscow");
        verify(userStore).save(any(User.class));
    }

    @Test
    void whenFindByMixedCaseLoginAndPasswordThenSearchByNormalizedLogin() {
        when(userStore.findByLoginAndPassword("user@mail.ru", "password")).thenReturn(Optional.empty());

        Optional<User> user = userService.findByLoginAndPassword(" User@mail.ru ", "password");

        assertThat(user).isEmpty();
        verify(userStore).findByLoginAndPassword("user@mail.ru", "password");
    }

    @Test
    void whenCreateInvalidUserThenDoNotCallStore() {
        Optional<User> user = userService.create(" ", "user@mail.ru", "password");

        assertThat(user).isEmpty();
        verifyNoInteractions(userStore);
    }

    @Test
    void whenCreateWithInvalidTimezoneThenDoNotCallStore() {
        Optional<User> user = userService.create("Anton", "user@mail.ru", "password", "Unknown/Zone");

        assertThat(user).isEmpty();
        verifyNoInteractions(userStore);
    }

    @Test
    void whenCreateExistingLoginThenReturnEmptyOptional() {
        when(userStore.save(any(User.class))).thenThrow(
                new org.hibernate.exception.ConstraintViolationException(
                        "duplicate login",
                        new SQLException(),
                        "todo_user_lower_login_unique_idx"
                )
        );

        Optional<User> user = userService.create("Anton", "User@mail.ru", "password");

        assertThat(user).isEmpty();
        verify(userStore).save(any(User.class));
    }

    @Test
    void whenCreateSaveFailsThenReturnEmptyOptional() {
        when(userStore.save(any(User.class))).thenThrow(new RuntimeException());

        Optional<User> user = userService.create("Anton", "User@mail.ru", "password");

        assertThat(user).isEmpty();
    }
}

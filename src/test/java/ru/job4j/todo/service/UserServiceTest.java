package ru.job4j.todo.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.UserStore;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        when(userStore.findByLogin("user@mail.ru")).thenReturn(Optional.empty());
        when(userStore.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> user = userService.create(" Anton ", " User@mail.ru ", "password");

        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Anton");
        assertThat(user.get().getLogin()).isEqualTo("user@mail.ru");
        verify(userStore).findByLogin("user@mail.ru");
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
    void whenCreateExistingLoginThenDoNotSaveUser() {
        var existingUser = User.builder()
                .id(1)
                .name("Anton")
                .login("user@mail.ru")
                .password("password")
                .build();
        when(userStore.findByLogin("user@mail.ru")).thenReturn(Optional.of(existingUser));

        Optional<User> user = userService.create("Anton", "User@mail.ru", "password");

        assertThat(user).isEmpty();
        verify(userStore, never()).save(any(User.class));
    }

    @Test
    void whenCreateSaveFailsThenReturnEmptyOptional() {
        when(userStore.findByLogin("user@mail.ru")).thenReturn(Optional.empty());
        when(userStore.save(any(User.class))).thenThrow(new RuntimeException());

        Optional<User> user = userService.create("Anton", "User@mail.ru", "password");

        assertThat(user).isEmpty();
    }
}

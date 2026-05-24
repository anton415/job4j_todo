package ru.job4j.todo.store;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.User;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrudRepositoryTest {

    @Test
    void whenOptionalQueryFailsThenReturnEmptyOptional() {
        var sessionFactory = mock(SessionFactory.class);
        when(sessionFactory.openSession()).thenThrow(new RuntimeException());
        var crudRepository = new CrudRepository(sessionFactory);

        var user = crudRepository.optional("from TodoUser", User.class, Map.of());

        assertThat(user).isEmpty();
    }
}

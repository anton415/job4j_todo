package ru.job4j.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.PriorityStore;
import ru.job4j.todo.store.TaskStore;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    private TaskStore taskStore;
    private PriorityStore priorityStore;
    private TaskService taskService;

    @BeforeEach
    void initService() {
        taskStore = mock(TaskStore.class);
        priorityStore = mock(PriorityStore.class);
        taskService = new TaskService(taskStore, priorityStore);
    }

    @Test
    void whenCreateThenSetNormalPriority() {
        var priority = Priority.builder()
                .id(2)
                .name("normal")
                .position(2)
                .build();
        var user = User.builder()
                .id(1)
                .name("Anton")
                .login("anton@mail.ru")
                .password("password")
                .build();
        when(priorityStore.findByName("normal")).thenReturn(Optional.of(priority));
        when(taskStore.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var task = taskService.create("Title", "Description", user);

        assertThat(task.getPriority()).isSameAs(priority);
        assertThat(task.getUser()).isSameAs(user);
        verify(priorityStore).findByName("normal");
        verify(taskStore).save(any(Task.class));
    }
}

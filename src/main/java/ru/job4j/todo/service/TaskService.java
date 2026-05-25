package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.PriorityStore;
import ru.job4j.todo.store.TaskStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String DEFAULT_PRIORITY = "normal";

    private final TaskStore taskStore;
    private final PriorityStore priorityStore;

    public List<Task> findAll() {
        return taskStore.findAll();
    }

    public List<Task> findCompleted() {
        return taskStore.findByDone(true);
    }

    public List<Task> findNew() {
        return taskStore.findByDone(false);
    }

    public Optional<Task> findById(int id) {
        return taskStore.findById(id);
    }

    public Task create(String title, String description, User user) {
        var task = Task.builder()
                .title(title)
                .description(description)
                .created(LocalDateTime.now())
                .done(false)
                .user(user)
                .priority(priorityStore.findByName(DEFAULT_PRIORITY)
                        .orElseThrow(() -> new IllegalStateException("Default priority is not found")))
                .build();
        return taskStore.save(task);
    }

    public boolean update(int id, String title, String description, boolean done) {
        var task = Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .done(done)
                .build();
        return taskStore.update(task);
    }

    public boolean setDone(int id) {
        return taskStore.setDone(id);
    }

    public boolean delete(int id) {
        return taskStore.delete(id);
    }
}

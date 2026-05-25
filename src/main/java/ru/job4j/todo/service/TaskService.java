package ru.job4j.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.model.Priority;
import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;
import ru.job4j.todo.store.CategoryStore;
import ru.job4j.todo.store.PriorityStore;
import ru.job4j.todo.store.TaskStore;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String DEFAULT_PRIORITY = "normal";

    private final TaskStore taskStore;
    private final PriorityStore priorityStore;
    private final CategoryStore categoryStore;

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

    public List<Category> findAllCategories() {
        return categoryStore.findAll();
    }

    public List<Priority> findAllPriorities() {
        return priorityStore.findAll();
    }

    public Task create(String title,
                       String description,
                       User user,
                       int priorityId,
                       List<Integer> categoryIds) {
        return create(
                title,
                description,
                user,
                priorityStore.findById(priorityId)
                        .orElseThrow(() -> new IllegalArgumentException("Priority is not found")),
                categoryIds
        );
    }

    public Task create(String title, String description, User user, List<Integer> categoryIds) {
        return create(
                title,
                description,
                user,
                priorityStore.findByName(DEFAULT_PRIORITY)
                        .orElseThrow(() -> new IllegalStateException("Default priority is not found")),
                categoryIds
        );
    }

    public Task create(String title, String description, User user) {
        return create(title, description, user, List.of());
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

    private Task create(String title,
                        String description,
                        User user,
                        Priority priority,
                        List<Integer> categoryIds) {
        var task = Task.builder()
                .title(title)
                .description(description)
                .created(LocalDateTime.now())
                .done(false)
                .user(user)
                .priority(priority)
                .categories(toCategories(categoryIds))
                .build();
        return taskStore.save(task);
    }

    private Set<Category> toCategories(List<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return categoryIds.stream()
                .distinct()
                .map(id -> Category.builder().id(id).build())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Priority;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PriorityStore {

    private final CrudRepository crudRepository;

    public Optional<Priority> findByName(String name) {
        return crudRepository.optional(
                "FROM Priority WHERE name = :name",
                Priority.class,
                Map.of("name", name)
        );
    }
}

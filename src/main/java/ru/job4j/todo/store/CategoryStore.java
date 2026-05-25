package ru.job4j.todo.store;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryStore {

    private final CrudRepository crudRepository;

    public List<Category> findAll() {
        return crudRepository.query(
                """
                        FROM Category
                        ORDER BY name
                        """,
                Category.class
        );
    }
}

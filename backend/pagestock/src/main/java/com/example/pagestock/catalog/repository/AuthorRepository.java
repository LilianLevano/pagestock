package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Author;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
    List<Author> findByNameContainingIgnoreCase(String name);
    boolean existsByNameEqualsIgnoreCase(String name);
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);
}

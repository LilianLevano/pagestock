package com.example.pagestock.repository;

import com.example.pagestock.model.Author;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AuthorRepository extends ListCrudRepository<Author, Long> {
    List<Author> findByNameContainingIgnoreCase(String name);
    boolean existsByNameContainingIgnoreCase(String name);
}

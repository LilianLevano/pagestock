package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Book;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface BookRepository extends ListCrudRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);

    boolean existsByCategoryId(Long categoryId);
    long countByCategoryId(Long categoryId);

    boolean existsByAuthorsId(Long authorId);
    long countByAuthorsId(Long authorId);
}

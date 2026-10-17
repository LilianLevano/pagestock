package com.example.pagestock.repository;

import com.example.pagestock.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface BookRepository extends ListCrudRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    boolean existsByCategoryId(Long categoryId);
    long countByCategoryId(Long categoryId);
}

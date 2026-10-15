package com.example.pagestock.repository;

import com.example.pagestock.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}

package com.example.pagestock.repository;

import com.example.pagestock.model.Category;
import org.springframework.data.repository.ListCrudRepository;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {
}

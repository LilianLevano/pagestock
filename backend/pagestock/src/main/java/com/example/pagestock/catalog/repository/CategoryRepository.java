package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Category;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CategoryRepository extends ListCrudRepository<Category, Long> {
    List<Category> findAllByOrderByIdAsc();
    List<Category> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}

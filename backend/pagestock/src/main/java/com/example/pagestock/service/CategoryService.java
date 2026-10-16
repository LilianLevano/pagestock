package com.example.pagestock.service;

import com.example.pagestock.dto.category.CategoryRequest;
import com.example.pagestock.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.CategoryAlreadyExistsException;
import com.example.pagestock.model.Category;
import com.example.pagestock.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<CategoryResponse> findById(Long id) {
        return categoryRepository.findById(id).map(this::toResponse);
    }

    public List<CategoryResponse> findByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    public CategoryResponse save(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new CategoryAlreadyExistsException(request.name());
        }
        Category category = new Category(request.name());
        return toResponse(categoryRepository.save(category));
    }

    public Optional<CategoryResponse> update (CategoryRequest request, Long id) {
        return categoryRepository.findById(id).map(category ->  {

            if (categoryRepository.existsByNameIgnoreCase(request.name())) {
                throw new CategoryAlreadyExistsException(request.name());
            }
            category.setName(request.name());
            return toResponse(categoryRepository.save(category));
        });
    }

    public boolean deleteById(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }

        return false;

    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getName()
        );
    }

}

package com.example.pagestock.service;

import com.example.pagestock.dto.category.CategoryRequest;
import com.example.pagestock.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.CategoryAlreadyExistsException;
import com.example.pagestock.exceptions.CategoryNotEmptyException;
import com.example.pagestock.model.Category;
import com.example.pagestock.repository.BookRepository;
import com.example.pagestock.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private BookRepository bookRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,  BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
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

        String cleanName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(cleanName)) {
            throw new CategoryAlreadyExistsException(cleanName);
        }
        Category category = new Category(cleanName);
        return toResponse(categoryRepository.save(category));
    }

    public Optional<CategoryResponse> update (CategoryRequest request, Long id) {
        return categoryRepository.findById(id).map(category ->  {

            String cleanName = request.name().trim();

            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(cleanName, id)) {
                throw new CategoryAlreadyExistsException(cleanName);
            }
            category.setName(cleanName);
            return toResponse(categoryRepository.save(category));
        });
    }

    public boolean deleteById(Long id) {

        if (!categoryRepository.existsById(id)) {
            return false;
        }

        if(bookRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmptyException(id, bookRepository.countByCategoryId(id));
        }

        categoryRepository.deleteById(id);
        return true;


    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                 category.getId(), category.getName()
        );
    }

}

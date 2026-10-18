package com.example.pagestock.catalog.service;

import com.example.pagestock.catalog.dto.category.CategoryRequest;
import com.example.pagestock.catalog.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.CategoryAlreadyExistsException;
import com.example.pagestock.exceptions.CategoryNotEmptyException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.catalog.model.Category;
import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.catalog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

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

    public CategoryResponse update (CategoryRequest request, Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Could not find category with id: " + id));

        String cleanName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(cleanName, id)) {
            throw new CategoryAlreadyExistsException(cleanName);
        }
        category.setName(cleanName);
        return toResponse(categoryRepository.save(category));
    }

    public void deleteById(Long id) {

        if (!categoryRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find category with id: " + id);
        }

        if(bookRepository.existsByCategoryId(id)) {
            throw new CategoryNotEmptyException(id, bookRepository.countByCategoryId(id));
        }

        categoryRepository.deleteById(id);

    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                 category.getId(), category.getName()
        );
    }

}

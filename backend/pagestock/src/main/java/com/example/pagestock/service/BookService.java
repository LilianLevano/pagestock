package com.example.pagestock.service;

import com.example.pagestock.dto.author.AuthorResponse;
import com.example.pagestock.dto.book.BookRequest;
import com.example.pagestock.dto.book.BookResponse;
import com.example.pagestock.dto.book.BookUpdateRequest;
import com.example.pagestock.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.BookAlreadyExistsException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.model.Author;
import com.example.pagestock.model.Book;
import com.example.pagestock.model.Category;
import com.example.pagestock.repository.AuthorRepository;
import com.example.pagestock.repository.BookRepository;
import com.example.pagestock.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,  AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }


    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<BookResponse> findById(String id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    public List<BookResponse> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toResponse).toList();
    }

    public BookResponse save(BookRequest bookRequest){

        if (bookRepository.existsById(bookRequest.isbnNumber())) {
            throw new BookAlreadyExistsException(bookRequest.isbnNumber());
        }

        Book book = new Book(bookRequest.isbnNumber(),
                bookRequest.title(),
                bookRequest.description(),
                bookRequest.price(),
                bookRequest.publicationDate(),
                bookRequest.stock());
        book.setCategory(findCategoryByIdOrThrow(bookRequest.categoryId()));
        book.setAuthors(findAuthorsByIdsOrThrow(bookRequest.authorIds()));


        return toResponse(bookRepository.save(book));
    }

    public Optional<BookResponse> update(BookUpdateRequest bookRequest, String isbnNumber) {
        return  bookRepository.findById(isbnNumber).map(book -> {
            book.setTitle(bookRequest.title());
            book.setDescription(bookRequest.description());
            book.setPrice(bookRequest.price());
            book.setPublicationDate(bookRequest.publicationDate());
            book.setStock(bookRequest.stock());
            book.setCategory(findCategoryByIdOrThrow(bookRequest.categoryId()));
            book.setAuthors(findAuthorsByIdsOrThrow(bookRequest.authorIds()));
            return toResponse(bookRepository.save(book));
        });
    }

    public void deleteById(String id) {

        if (!bookRepository.existsById(id)) {
            throw new ItemNotFoundException("Could not find Book with id: " + id);
        }

        bookRepository.deleteById(id);

    }

    private Category findCategoryByIdOrThrow(Long id) {
        return  categoryRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Category with id: " + id + " not found"));
    }

    private Set<Author> findAuthorsByIdsOrThrow(Set<Long> ids) {
        Set<Author> authors = new HashSet<>();
        for (Long id : ids) {
            authors.add(authorRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Author with id: " + id + " not found")));
        }

        return authors;
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getIsbnNumber(),
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getStock(),
                new CategoryResponse(book.getCategory().getId(), book.getCategory().getName()),
                book.getAuthors().stream().map(author -> new AuthorResponse(author.getId(), author.getName())).toList()
        );
    }
}

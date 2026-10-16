package com.example.pagestock.service;

import com.example.pagestock.dto.book.BookRequest;
import com.example.pagestock.dto.book.BookResponse;
import com.example.pagestock.model.Book;
import com.example.pagestock.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
            throw new IllegalStateException("Book already exists: " + bookRequest.isbnNumber());
        }

        Book book = new Book(bookRequest.isbnNumber(),
                bookRequest.title(),
                bookRequest.description(),
                bookRequest.price(),
                bookRequest.publicationDate(),
                bookRequest.stock());
        return toResponse(bookRepository.save(book));
    }

    public Optional<BookResponse> update(BookRequest bookRequest, String isbnNumber) {
        return  bookRepository.findById(isbnNumber).map(book -> {
            book.setTitle(bookRequest.title());
            book.setDescription(bookRequest.description());
            book.setPrice(bookRequest.price());
            book.setPublicationDate(bookRequest.publicationDate());
            book.setStock(bookRequest.stock());
            return toResponse(bookRepository.save(book));
        });
    }

    public boolean deleteById(String id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getIsbnNumber(),
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getStock()
        );
    }
}

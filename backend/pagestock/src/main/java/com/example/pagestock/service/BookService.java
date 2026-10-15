package com.example.pagestock.service;

import com.example.pagestock.dto.book.BookRequest;
import com.example.pagestock.dto.book.BookResponse;
import com.example.pagestock.model.Book;
import com.example.pagestock.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> findAll(){
        return bookRepository.findAll();
    }

    public BookResponse save(BookRequest bookRequest){

        Book book = new Book(bookRequest.ISBN_number(),
                bookRequest.title(),
                bookRequest.description(),
                bookRequest.price(),
                bookRequest.publicationDate(),
                bookRequest.stock());
        return toResponse(bookRepository.save(book));
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getISBN_number(),
                book.getTitle(),
                book.getDescription(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getStock()
        );
    }
}

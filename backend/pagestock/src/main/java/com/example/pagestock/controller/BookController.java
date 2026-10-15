package com.example.pagestock.controller;

import com.example.pagestock.dto.book.BookRequest;
import com.example.pagestock.dto.book.BookResponse;
import com.example.pagestock.model.Book;
import com.example.pagestock.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getBooks(){
        return (List<Book>) bookService.findAll();
    }

    @PostMapping
    public BookResponse addBook(@Valid @RequestBody BookRequest request){
        return bookService.save(request);
    }

}

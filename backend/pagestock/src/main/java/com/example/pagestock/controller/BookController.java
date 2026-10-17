package com.example.pagestock.controller;

import com.example.pagestock.dto.book.BookRequest;
import com.example.pagestock.dto.book.BookResponse;
import com.example.pagestock.dto.book.BookUpdateRequest;
import com.example.pagestock.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }




    @GetMapping
    public ResponseEntity<List<BookResponse>> findAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable String id) {
        return bookService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> findBookByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findByTitle(title));
    }

    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(bookRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String id, @Valid @RequestBody BookUpdateRequest bookRequest) {
        return ResponseEntity.ok(bookService.update(bookRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();      // 204
    }


}

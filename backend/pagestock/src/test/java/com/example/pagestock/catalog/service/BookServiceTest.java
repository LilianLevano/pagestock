package com.example.pagestock.catalog.service;

import com.example.pagestock.catalog.model.Book;
import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.exceptions.OutOfStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Mockito activeren: geen Spring-context, geen databank nodig
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // Nep-repository: we bepalen zelf wat die teruggeeft
    @Mock
    private BookRepository bookRepository;

    // Echte BookService, met de nep-repository erin geïnjecteerd (via de constructor)
    @InjectMocks
    private BookService bookService;

    @Test
    void decreaseStock_voldoendeVoorraad_verlaagtVoorraad() {
        // Arrange: een boek met voorraad 5
        Book book = new Book();
        book.setIsbnNumber("9780132350884");
        book.setStock(5L);

        // Als de service het boek opvraagt, geeft de mock ons boek terug
        when(bookRepository.findById("9780132350884")).thenReturn(Optional.of(book));

        // Act: 2 exemplaren afhalen
        bookService.decreaseStock("9780132350884", 2);

        // Assert: de voorraad is 5 - 2 = 3
        assertEquals(3, book.getStock());

    }

    @Test
    void decreaseStock_teWeinigVoorraad_gooitOutOfStockException() {
        // Arrange: een boek met voorraad 1
        Book book = new Book();
        book.setIsbnNumber("9780132350884");
        book.setStock(1L);

        when(bookRepository.findById("9780132350884")).thenReturn(Optional.of(book));

        // Act + Assert: 2 exemplaren afhalen moet een OutOfStockException gooien
        assertThrows(OutOfStockException.class,
                () -> bookService.decreaseStock("9780132350884", 2));

        // De voorraad mag niet veranderd zijn
        assertEquals(1, book.getStock());
    }
}
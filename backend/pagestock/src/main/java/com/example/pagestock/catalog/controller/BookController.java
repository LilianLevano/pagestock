package com.example.pagestock.catalog.controller;

import com.example.pagestock.catalog.dto.book.BookRequest;
import com.example.pagestock.catalog.dto.book.BookResponse;
import com.example.pagestock.catalog.dto.book.BookUpdateRequest;
import com.example.pagestock.catalog.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST-controller voor de boeken uit de catalogus, bereikbaar onder {@code /books}.
 * <p>
 * Toegang (zie {@code SecurityConfig}):
 * <ul>
 *   <li>{@code GET}-requests zijn publiek toegankelijk.</li>
 *   <li>{@code POST}, {@code PUT} en {@code DELETE} zijn alleen voor gebruikers met de rol {@code ADMIN}.
 *       Zonder geldig JWT-token volgt {@code 401 Unauthorized}, zonder de rol {@code ADMIN}
 *       {@code 403 Forbidden}.</li>
 * </ul>
 * Bij validatiefouten in de request body volgt {@code 400 Bad Request} met de foutmeldingen
 * per veld.
 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    /**
     * Maakt de controller aan.
     *
     * @param bookService service met de business logic voor boeken
     */
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Haalt alle boeken op, alfabetisch gesorteerd op titel.
     * <p>
     * Endpoint: {@code GET /books} — publiek.
     *
     * @return {@code 200 OK} met de lijst van boeken; leeg als er geen boeken zijn
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> findAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    /**
     * Haalt één boek op via zijn ISBN-nummer.
     * <p>
     * Endpoint: {@code GET /books/{id}} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 404 Not Found} — er bestaat geen boek met dit ISBN-nummer (lege body)</li>
     * </ul>
     *
     * @param id ISBN-nummer van het boek
     * @return {@code 200 OK} met het gevonden boek
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable String id) {
        return bookService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Zoekt boeken waarvan de titel de opgegeven tekst bevat, ongeacht hoofdletters.
     * <p>
     * Endpoint: {@code GET /books/search?title=...} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de parameter {@code title} ontbreekt</li>
     * </ul>
     *
     * @param title (deel van de) titel om op te zoeken
     * @return {@code 200 OK} met de overeenkomende boeken; leeg als er geen overeenkomsten zijn
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> findBookByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findByTitle(title));
    }

    /**
     * Maakt een nieuw boek aan.
     * <p>
     * Endpoint: {@code POST /books} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — ongeldige body (bv. verkeerd ISBN-formaat, negatieve prijs, geen auteurs)</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — de categorie of een van de auteurs bestaat niet</li>
     *   <li>{@code 409 Conflict} — er bestaat al een boek met dit ISBN-nummer</li>
     * </ul>
     *
     * @param bookRequest gegevens van het nieuwe boek
     * @return {@code 201 Created} met het aangemaakte boek
     */
    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(bookRequest));
    }

    /**
     * Werkt een bestaand boek volledig bij; alleen het ISBN-nummer blijft ongewijzigd.
     * <p>
     * Endpoint: {@code PUT /books/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — ongeldige body</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — het boek, de categorie of een van de auteurs bestaat niet</li>
     * </ul>
     *
     * @param id          ISBN-nummer van het boek dat moet worden bijgewerkt
     * @param bookRequest nieuwe gegevens van het boek
     * @return {@code 200 OK} met het bijgewerkte boek
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable String id, @Valid @RequestBody BookUpdateRequest bookRequest) {
        return ResponseEntity.ok(bookService.update(bookRequest, id));
    }

    /**
     * Verwijdert een boek.
     * <p>
     * Endpoint: {@code DELETE /books/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — er bestaat geen boek met dit ISBN-nummer</li>
     * </ul>
     *
     * @param id ISBN-nummer van het boek dat moet worden verwijderd
     * @return {@code 204 No Content} als het boek is verwijderd
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();      // 204
    }


}

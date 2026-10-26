package com.example.pagestock.catalog.service;

import com.example.pagestock.catalog.dto.author.AuthorResponse;
import com.example.pagestock.catalog.dto.book.BookRequest;
import com.example.pagestock.catalog.dto.book.BookResponse;
import com.example.pagestock.catalog.dto.book.BookUpdateRequest;
import com.example.pagestock.catalog.dto.category.CategoryResponse;
import com.example.pagestock.exceptions.BookAlreadyExistsException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.catalog.model.Author;
import com.example.pagestock.catalog.model.Book;
import com.example.pagestock.catalog.model.Category;
import com.example.pagestock.catalog.repository.AuthorRepository;
import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.catalog.repository.CategoryRepository;
import com.example.pagestock.exceptions.OutOfStockException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Business logic voor het beheren van boeken in de catalogus.
 * <p>
 * Zet {@link BookRequest}- en {@link BookUpdateRequest}-objecten om naar {@link Book}-entiteiten,
 * koppelt de bijbehorende {@link Category} en {@link Author}s, en geeft resultaten terug als
 * {@link BookResponse}. Beheert ook de voorraad van een boek.
 */
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    /**
     * Maakt de service aan met de benodigde repositories.
     *
     * @param bookRepository     repository voor boeken
     * @param categoryRepository repository om de categorie van een boek op te halen
     * @param authorRepository   repository om de auteurs van een boek op te halen
     */
    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,  AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }


    /**
     * Haalt alle boeken op, alfabetisch gesorteerd op titel.
     *
     * @return lijst van alle boeken; leeg als er geen boeken zijn
     */
    public List<BookResponse> findAll() {
        return bookRepository.findAllByOrderByTitleAsc().stream().map(this::toResponse).toList();
    }

    /**
     * Zoekt een boek op basis van zijn ISBN-nummer.
     *
     * @param id ISBN-nummer van het boek
     * @return het gevonden boek, of {@link Optional#empty()} als er geen boek met dit ISBN-nummer bestaat
     */
    public Optional<BookResponse> findById(String id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    /**
     * Zoekt boeken waarvan de titel de opgegeven tekst bevat, ongeacht hoofdletters.
     *
     * @param title (deel van de) titel om op te zoeken
     * @return lijst van overeenkomende boeken; leeg als er geen overeenkomsten zijn
     */
    public List<BookResponse> findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream().map(this::toResponse).toList();
    }

    /**
     * Maakt een nieuw boek aan en koppelt de opgegeven categorie en auteurs.
     *
     * @param bookRequest gegevens van het nieuwe boek, inclusief categorie-id en auteur-id's
     * @return het opgeslagen boek
     * @throws BookAlreadyExistsException als er al een boek met hetzelfde ISBN-nummer bestaat
     * @throws ItemNotFoundException      als de categorie of een van de auteurs niet bestaat
     */
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

    /**
     * Werkt een bestaand boek bij.
     * <p>
     * Alle velden behalve het ISBN-nummer worden overschreven, inclusief de categorie
     * en de volledige set auteurs.
     *
     * @param bookRequest nieuwe gegevens van het boek
     * @param isbnNumber  ISBN-nummer van het boek dat moet worden bijgewerkt
     * @return het bijgewerkte boek
     * @throws ItemNotFoundException als het boek, de categorie of een van de auteurs niet bestaat
     */
    public BookResponse update(BookUpdateRequest bookRequest, String isbnNumber) {

        Book book = bookRepository.findById(isbnNumber)
                .orElseThrow(() -> new ItemNotFoundException("Could not find book with id: " + isbnNumber));

        book.setTitle(bookRequest.title());
        book.setDescription(bookRequest.description());
        book.setPrice(bookRequest.price());
        book.setPublicationDate(bookRequest.publicationDate());
        book.setStock(bookRequest.stock());
        book.setCategory(findCategoryByIdOrThrow(bookRequest.categoryId()));
        book.setAuthors(findAuthorsByIdsOrThrow(bookRequest.authorIds()));
        return toResponse(bookRepository.save(book));


    }

    /**
     * Verwijdert een boek op basis van zijn ISBN-nummer.
     *
     * @param id ISBN-nummer van het boek dat moet worden verwijderd
     * @throws ItemNotFoundException als er geen boek met dit ISBN-nummer bestaat
     */
    public void delete(String id) {

        if (!bookRepository.existsById(id)) {
            throw new ItemNotFoundException("Could not find Book with id: " + id);
        }

        bookRepository.deleteById(id);

    }

    /**
     * Verlaagt de voorraad van een boek met het opgegeven aantal.
     * <p>
     * Draait binnen een transactie: de nieuwe voorraad wordt via dirty checking opgeslagen
     * bij het afsluiten van de transactie, zonder expliciete {@code save}.
     *
     * @param isbnNumber ISBN-nummer van het boek
     * @param quantity   aantal exemplaren dat van de voorraad wordt afgetrokken; moet groter zijn dan 0
     * @throws IllegalArgumentException als {@code quantity} 0 of negatief is
     * @throws ItemNotFoundException    als er geen boek met dit ISBN-nummer bestaat
     * @throws OutOfStockException      als de voorraad onvoldoende is voor het gevraagde aantal
     */
    @Transactional
    public void decreaseStock(String isbnNumber, int quantity) {

        if(quantity <= 0) {
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        Book book = bookRepository.findById(isbnNumber).orElseThrow(() -> new ItemNotFoundException("Could not find book with id: " + isbnNumber));

        if (book.getStock() - quantity < 0) {
            throw new OutOfStockException(book.getIsbnNumber(), book.getStock());
        }

        book.setStock(book.getStock() - quantity);
    }

    /**
     * Haalt een categorie op of gooit een exception als die niet bestaat.
     *
     * @param id id van de categorie
     * @return de gevonden categorie
     * @throws ItemNotFoundException als er geen categorie met dit id bestaat
     */
    private Category findCategoryByIdOrThrow(Long id) {
        return  categoryRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Category with id: " + id + " not found"));
    }

    /**
     * Haalt alle auteurs met de opgegeven id's op.
     *
     * @param ids id's van de auteurs
     * @return set met de gevonden auteurs
     * @throws ItemNotFoundException bij het eerste id waarvoor geen auteur bestaat
     */
    private Set<Author> findAuthorsByIdsOrThrow(Set<Long> ids) {
        Set<Author> authors = new HashSet<>();
        for (Long id : ids) {
            authors.add(authorRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Author with id: " + id + " not found")));
        }

        return authors;
    }

    /**
     * Zet een {@link Book}-entiteit om naar een {@link BookResponse}, inclusief categorie en auteurs.
     *
     * @param book het om te zetten boek
     * @return de response-representatie van het boek
     */
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

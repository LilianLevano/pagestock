package com.example.pagestock.catalog.service;

import com.example.pagestock.catalog.dto.author.AuthorRequest;
import com.example.pagestock.catalog.dto.author.AuthorResponse;
import com.example.pagestock.exceptions.AuthorAlreadyExistsException;
import com.example.pagestock.exceptions.AuthorNotEmptyException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.catalog.model.Author;
import com.example.pagestock.catalog.repository.AuthorRepository;
import com.example.pagestock.catalog.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic voor het beheren van auteurs in de catalogus.
 * <p>
 * Zorgt ervoor dat auteursnamen uniek blijven (ongeacht hoofdletters) en dat een auteur
 * niet kan worden verwijderd zolang er nog boeken aan gekoppeld zijn.
 */
@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    /**
     * Maakt de service aan met de benodigde repositories.
     *
     * @param authorRepository repository voor auteurs
     * @param bookRepository   repository om te controleren of een auteur nog boeken heeft
     */
    @Autowired
    public AuthorService(AuthorRepository authorRepository,  BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Haalt alle auteurs op, gesorteerd op id.
     *
     * @return lijst van alle auteurs; leeg als er geen auteurs zijn
     */
    public List<AuthorResponse> findAll() {
        return authorRepository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
    }

    /**
     * Zoekt een auteur op basis van zijn id.
     *
     * @param id id van de auteur
     * @return de gevonden auteur, of {@link Optional#empty()} als er geen auteur met dit id bestaat
     */
    public Optional<AuthorResponse> findById(Long id) {
        return authorRepository.findById(id).map(this::toResponse);
    }

    /**
     * Zoekt auteurs waarvan de naam de opgegeven tekst bevat, ongeacht hoofdletters.
     *
     * @param name (deel van de) naam om op te zoeken
     * @return lijst van overeenkomende auteurs; leeg als er geen overeenkomsten zijn
     */
    public List<AuthorResponse> findByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    /**
     * Maakt een nieuwe auteur aan. Spaties vooraan en achteraan de naam worden verwijderd.
     *
     * @param authorRequest gegevens van de nieuwe auteur
     * @return de opgeslagen auteur
     * @throws AuthorAlreadyExistsException als er al een auteur met dezelfde naam bestaat (ongeacht hoofdletters)
     */
    public AuthorResponse save(AuthorRequest authorRequest) {
        if (authorRepository.existsByNameEqualsIgnoreCase(authorRequest.name().trim())){
            throw new AuthorAlreadyExistsException(authorRequest.name());
        }
        Author author = new Author(authorRequest.name().trim());

        return toResponse(authorRepository.save(author));
    }

    /**
     * Wijzigt de naam van een bestaande auteur. Spaties vooraan en achteraan de naam worden verwijderd.
     *
     * @param authorRequest nieuwe gegevens van de auteur
     * @param id            id van de auteur die moet worden bijgewerkt
     * @return de bijgewerkte auteur
     * @throws ItemNotFoundException        als er geen auteur met dit id bestaat
     * @throws AuthorAlreadyExistsException als een andere auteur al dezelfde naam heeft (ongeacht hoofdletters)
     */
    public AuthorResponse update(AuthorRequest authorRequest, Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Could not find author with id: " + id));


        String name = authorRequest.name().trim();

        if (authorRepository.existsByNameEqualsIgnoreCaseAndIdNot(name, id)){
            throw new AuthorAlreadyExistsException(name);
        }

        author.setName(name);
        return toResponse(authorRepository.save(author));
    }

    /**
     * Verwijdert een auteur, op voorwaarde dat er geen boeken meer aan gekoppeld zijn.
     *
     * @param id id van de auteur die moet worden verwijderd
     * @throws ItemNotFoundException   als er geen auteur met dit id bestaat
     * @throws AuthorNotEmptyException als er nog boeken aan de auteur gekoppeld zijn
     */
    public void delete(Long id) {
        if (!authorRepository.existsById(id)){
            throw new ItemNotFoundException("Could not find author with id: " + id);
        }

        if (bookRepository.existsByAuthorsId(id)){
            throw  new AuthorNotEmptyException(id, bookRepository.countByAuthorsId(id));
        }

        authorRepository.deleteById(id);
    }

    /**
     * Zet een {@link Author}-entiteit om naar een {@link AuthorResponse}.
     *
     * @param author de om te zetten auteur
     * @return de response-representatie van de auteur
     */
    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }




}

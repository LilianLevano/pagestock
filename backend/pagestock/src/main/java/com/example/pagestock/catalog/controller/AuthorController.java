package com.example.pagestock.catalog.controller;

import com.example.pagestock.catalog.dto.author.AuthorRequest;
import com.example.pagestock.catalog.dto.author.AuthorResponse;
import com.example.pagestock.catalog.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller voor de auteurs uit de catalogus, bereikbaar onder {@code /authors}.
 * <p>
 * Toegang (zie {@code SecurityConfig}):
 * <ul>
 *   <li>{@code GET}-requests zijn publiek toegankelijk.</li>
 *   <li>{@code POST}, {@code PUT} en {@code DELETE} zijn alleen voor gebruikers met de rol {@code ADMIN}.
 *       Zonder geldig JWT-token volgt {@code 401 Unauthorized}, zonder de rol {@code ADMIN}
 *       {@code 403 Forbidden}.</li>
 * </ul>
 * Een {@code id} dat geen getal is (bv. {@code /authors/abc}) geeft {@code 400 Bad Request}.
 */
@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Maakt de controller aan.
     *
     * @param authorService service met de business logic voor auteurs
     */
    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * Haalt alle auteurs op, gesorteerd op id.
     * <p>
     * Endpoint: {@code GET /authors} — publiek.
     *
     * @return {@code 200 OK} met de lijst van auteurs; leeg als er geen auteurs zijn
     */
    @GetMapping
    public ResponseEntity<List<AuthorResponse>> findAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    /**
     * Haalt één auteur op via zijn id.
     * <p>
     * Endpoint: {@code GET /authors/{id}} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 404 Not Found} — er bestaat geen auteur met dit id (lege body)</li>
     * </ul>
     *
     * @param id id van de auteur
     * @return {@code 200 OK} met de gevonden auteur
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> findById(@PathVariable Long id) {
        return authorService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Zoekt auteurs waarvan de naam de opgegeven tekst bevat, ongeacht hoofdletters.
     * <p>
     * Endpoint: {@code GET /authors/search?name=...} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de parameter {@code name} ontbreekt</li>
     * </ul>
     *
     * @param name (deel van de) naam om op te zoeken
     * @return {@code 200 OK} met de overeenkomende auteurs; leeg als er geen overeenkomsten zijn
     */
    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponse>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(authorService.findByName(name));
    }

    /**
     * Maakt een nieuwe auteur aan.
     * <p>
     * Endpoint: {@code POST /authors} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de naam is leeg</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 409 Conflict} — er bestaat al een auteur met deze naam (ongeacht hoofdletters)</li>
     * </ul>
     *
     * @param authorRequest gegevens van de nieuwe auteur
     * @return {@code 201 Created} met de aangemaakte auteur
     */
    @PostMapping
    public ResponseEntity<AuthorResponse> save(@Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.save(authorRequest));
    }

    /**
     * Wijzigt de naam van een bestaande auteur.
     * <p>
     * Endpoint: {@code PUT /authors/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de naam is leeg</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — er bestaat geen auteur met dit id</li>
     *   <li>{@code 409 Conflict} — een andere auteur heeft al deze naam (ongeacht hoofdletters)</li>
     * </ul>
     *
     * @param id            id van de auteur die moet worden bijgewerkt
     * @param authorRequest nieuwe gegevens van de auteur
     * @return {@code 200 OK} met de bijgewerkte auteur
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> update(@PathVariable Long id, @Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.ok(authorService.update(authorRequest, id));
    }

    /**
     * Verwijdert een auteur die aan geen enkel boek meer gekoppeld is.
     * <p>
     * Endpoint: {@code DELETE /authors/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — er bestaat geen auteur met dit id</li>
     *   <li>{@code 409 Conflict} — er zijn nog boeken aan deze auteur gekoppeld</li>
     * </ul>
     *
     * @param id id van de auteur die moet worden verwijderd
     * @return {@code 204 No Content} als de auteur is verwijderd
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

package com.example.pagestock.catalog.controller;

import com.example.pagestock.catalog.dto.category.CategoryRequest;
import com.example.pagestock.catalog.dto.category.CategoryResponse;
import com.example.pagestock.catalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller voor de categorieën uit de catalogus, bereikbaar onder {@code /categories}.
 * <p>
 * Toegang (zie {@code SecurityConfig}):
 * <ul>
 *   <li>{@code GET}-requests zijn publiek toegankelijk.</li>
 *   <li>{@code POST}, {@code PUT} en {@code DELETE} zijn alleen voor gebruikers met de rol {@code ADMIN}.
 *       Zonder geldig JWT-token volgt {@code 401 Unauthorized}, zonder de rol {@code ADMIN}
 *       {@code 403 Forbidden}.</li>
 * </ul>
 * Een {@code id} dat geen getal is (bv. {@code /categories/abc}) geeft {@code 400 Bad Request}.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Maakt de controller aan.
     *
     * @param categoryService service met de business logic voor categorieën
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Haalt alle categorieën op, gesorteerd op id.
     * <p>
     * Endpoint: {@code GET /categories} — publiek.
     *
     * @return {@code 200 OK} met de lijst van categorieën; leeg als er geen categorieën zijn
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /**
     * Haalt één categorie op via haar id.
     * <p>
     * Endpoint: {@code GET /categories/{id}} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 404 Not Found} — er bestaat geen categorie met dit id (lege body)</li>
     * </ul>
     *
     * @param id id van de categorie
     * @return {@code 200 OK} met de gevonden categorie
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return categoryService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Zoekt categorieën waarvan de naam de opgegeven tekst bevat, ongeacht hoofdletters.
     * <p>
     * Endpoint: {@code GET /categories/search?name=...} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de parameter {@code name} ontbreekt</li>
     * </ul>
     *
     * @param name (deel van de) naam om op te zoeken
     * @return {@code 200 OK} met de overeenkomende categorieën; leeg als er geen overeenkomsten zijn
     */
    @GetMapping("/search")
    public ResponseEntity<List<CategoryResponse>> findByNameContainingIgnoreCase(@RequestParam String name) {
        return ResponseEntity.ok(categoryService.findByName(name));
    }

    /**
     * Maakt een nieuwe categorie aan.
     * <p>
     * Endpoint: {@code POST /categories} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de naam is leeg</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 409 Conflict} — er bestaat al een categorie met deze naam (ongeacht hoofdletters)</li>
     * </ul>
     *
     * @param categoryRequest gegevens van de nieuwe categorie
     * @return {@code 201 Created} met de aangemaakte categorie
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> save(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categoryRequest));
    }

    /**
     * Wijzigt de naam van een bestaande categorie.
     * <p>
     * Endpoint: {@code PUT /categories/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — de naam is leeg</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — er bestaat geen categorie met dit id</li>
     *   <li>{@code 409 Conflict} — een andere categorie heeft al deze naam (ongeacht hoofdletters)</li>
     * </ul>
     *
     * @param id              id van de categorie die moet worden bijgewerkt
     * @param categoryRequest nieuwe gegevens van de categorie
     * @return {@code 200 OK} met de bijgewerkte categorie
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.update(categoryRequest, id));
    }

    /**
     * Verwijdert een categorie die geen boeken meer bevat.
     * <p>
     * Endpoint: {@code DELETE /categories/{id}} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     *   <li>{@code 404 Not Found} — er bestaat geen categorie met dit id</li>
     *   <li>{@code 409 Conflict} — er zitten nog boeken in deze categorie</li>
     * </ul>
     *
     * @param id id van de categorie die moet worden verwijderd
     * @return {@code 204 No Content} als de categorie is verwijderd
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.pagestock.review.controller;

import com.example.pagestock.review.dto.CreateReviewRequest;
import com.example.pagestock.review.dto.ReviewResponse;
import com.example.pagestock.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller voor reviews op boeken.
 * <p>
 * Staat onder {@code /books} omdat een review altijd bij een boek hoort
 * ({@code /books/{isbn}/reviews}).
 * <p>
 * Toegang (zie {@code SecurityConfig}):
 * <ul>
 *   <li>{@code GET /books/{isbn}/reviews}: publiek.</li>
 *   <li>{@code POST /books/{isbn}/reviews} en {@code DELETE /books/reviews/{id}}: elke ingelogde
 *       gebruiker. De gebruiker wordt bepaald via het e-mailadres in het JWT-token
 *       ({@link Authentication#getName()}).</li>
 * </ul>
 * Zonder geldig JWT-token volgt {@code 401 Unauthorized}.
 */
@RestController
@RequestMapping("/books")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Maakt de controller aan.
     *
     * @param reviewService service met de business logic voor reviews
     */
    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Haalt alle reviews van een boek op, de meest recente eerst.
     * <p>
     * Endpoint: {@code GET /books/{isbn}/reviews} — publiek.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 404 Not Found} — er bestaat geen boek met dit ISBN-nummer</li>
     * </ul>
     *
     * @param isbn ISBN-nummer van het boek
     * @return {@code 200 OK} met de reviews; leeg als het boek nog geen reviews heeft
     */
    @GetMapping("/{isbn}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable String isbn) {
        return ResponseEntity.ok(reviewService.findByBookIsbn(isbn));
    }

    /**
     * Plaatst een review op een boek namens de ingelogde gebruiker.
     * <p>
     * Endpoint: {@code POST /books/{isbn}/reviews} — ingelogde gebruiker die het boek heeft opgehaald.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — ongeldige body (score niet tussen 1 en 5, lege of te lange tekst)</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — de gebruiker heeft het boek nog niet opgehaald</li>
     *   <li>{@code 404 Not Found} — er bestaat geen boek met dit ISBN-nummer</li>
     *   <li>{@code 409 Conflict} — de gebruiker heeft al een review op dit boek geplaatst</li>
     * </ul>
     *
     * @param isbn           ISBN-nummer van het boek
     * @param request        score en tekst van de review
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @return {@code 201 Created} met de geplaatste review
     */
    @PostMapping("/{isbn}/reviews")
    public ResponseEntity<ReviewResponse> create(@PathVariable String isbn,
                                                 @Valid @RequestBody CreateReviewRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        ReviewResponse response = reviewService.create(isbn, email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verwijdert een review. De auteur van de review en een {@code ADMIN} mogen dit doen.
     * <p>
     * Of de gebruiker {@code ADMIN} is, wordt hier bepaald op basis van de autoriteit
     * {@code ROLE_ADMIN} uit het token, en daarna aan de service doorgegeven.
     * <p>
     * Endpoint: {@code DELETE /books/reviews/{id}} — ingelogde gebruiker (auteur) of {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — het {@code id} is geen getal</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — de gebruiker is geen auteur van de review en geen {@code ADMIN}</li>
     *   <li>{@code 404 Not Found} — er bestaat geen review met dit id</li>
     * </ul>
     *
     * @param id             id van de review
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @return {@code 204 No Content} als de review is verwijderd
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        reviewService.deleteReview(id, authentication.getName(), isAdmin);

        return ResponseEntity.noContent().build();
    }
}
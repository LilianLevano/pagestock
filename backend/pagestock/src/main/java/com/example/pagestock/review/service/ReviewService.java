package com.example.pagestock.review.service;

import com.example.pagestock.catalog.dto.book.BookResponse;
import com.example.pagestock.catalog.service.BookService;
import com.example.pagestock.exceptions.DuplicateReviewException;
import com.example.pagestock.exceptions.ItemNotFoundException;
import com.example.pagestock.exceptions.ReviewDeleteNotAllowedException;
import com.example.pagestock.exceptions.ReviewNotAllowedException;
import com.example.pagestock.reservation.service.ReservationService;
import com.example.pagestock.review.dto.CreateReviewRequest;
import com.example.pagestock.review.dto.ReviewResponse;
import com.example.pagestock.review.model.Review;
import com.example.pagestock.review.repository.ReviewRepository;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic voor reviews: plaatsen, opvragen en verwijderen.
 * <p>
 * Regels:
 * <ul>
 *   <li>Alleen wie een boek heeft opgehaald (reservatie met status {@code PICKED_UP}) mag er een
 *       review over schrijven.</li>
 *   <li>Een gebruiker kan maximaal één review per boek plaatsen.</li>
 *   <li>Een review kan worden verwijderd door de auteur ervan of door een {@code ADMIN}.</li>
 * </ul>
 * Voor boeken, gebruikers en reservaties gaat deze service via {@link BookService},
 * {@link UserService} en {@link ReservationService}, niet rechtstreeks via hun repositories.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;
    private final ReservationService reservationService;

    /**
     * Maakt de service aan met de benodigde afhankelijkheden.
     *
     * @param reviewRepository   repository voor reviews
     * @param bookService        service om te controleren of een boek bestaat
     * @param userService        service om gebruikers en hun naam op te halen
     * @param reservationService service om te controleren of een gebruiker een boek heeft opgehaald
     */
    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookService bookService, UserService userService, ReservationService reservationService) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    /**
     * Plaatst een nieuwe review op een boek namens de ingelogde gebruiker.
     *
     * @param bookIsbn            ISBN-nummer van het boek
     * @param email               e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @param createReviewRequest score en tekst van de review
     * @return de opgeslagen review
     * @throws ItemNotFoundException      als het boek of de gebruiker niet bestaat
     * @throws ReviewNotAllowedException  als de gebruiker het boek nog niet heeft opgehaald
     * @throws DuplicateReviewException   als de gebruiker al een review op dit boek heeft geplaatst
     */
    public ReviewResponse create (String bookIsbn, String email, CreateReviewRequest createReviewRequest) {

        BookResponse book = bookService.findById(bookIsbn).orElseThrow(() ->  new ItemNotFoundException("Book " + bookIsbn + " not found"));
        User user = userService.findByEmail(email).orElseThrow(() ->  new ItemNotFoundException("User " + email + " not found"));

        if (!reservationService.hasPickedUp(user.getId(), bookIsbn)){
            throw new ReviewNotAllowedException("The book " + bookIsbn + " is not picked up. You cannot post a review about a book you don't have.");
        }

        if(reviewRepository.existsByBookIsbnAndUserId(bookIsbn, user.getId())){
            throw new DuplicateReviewException("You cannot post a second review on a book.");
        }

        Review review = new Review(createReviewRequest.comment(), createReviewRequest.rating(), book.isbnNumber(), user.getId(), LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }

    /**
     * Haalt alle reviews van een boek op, de meest recente eerst.
     *
     * @param bookIsbn ISBN-nummer van het boek
     * @return de reviews van het boek; leeg als er nog geen zijn
     * @throws ItemNotFoundException als het boek niet bestaat
     */
    public List<ReviewResponse>  findByBookIsbn(String bookIsbn) {
        if (bookService.findById(bookIsbn).isEmpty()) {
            throw new ItemNotFoundException("Book " + bookIsbn + " not found.");
        }

        return reviewRepository.findByBookIsbnOrderByCreatedAtDesc(bookIsbn).stream().map(this::toResponse).toList();
    }

    /**
     * Verwijdert een review. Alleen de auteur van de review of een {@code ADMIN} mag dit doen.
     *
     * @param id      id van de review
     * @param email   e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @param isAdmin {@code true} als de ingelogde gebruiker de rol {@code ADMIN} heeft; wordt
     *                door de controller bepaald
     * @throws ItemNotFoundException            als de review of de gebruiker niet bestaat
     * @throws ReviewDeleteNotAllowedException  als de gebruiker geen auteur van de review en geen
     *                                          {@code ADMIN} is
     */
    public void deleteReview(Long id, String email, boolean isAdmin) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->  new ItemNotFoundException("Review " + id + " not found"));

        User user = userService.findByEmail(email).orElseThrow(() ->  new ItemNotFoundException("User " + email + " not found"));

        if ( !isAdmin && !user.getId().equals(review.getUserId())){
            throw new ReviewDeleteNotAllowedException("You cannot delete the review that you don't own.");
        }

        reviewRepository.deleteById(id);
    }

    /**
     * Zet een {@link Review}-entiteit om naar een {@link ReviewResponse}, met de volledige naam
     * van de auteur in plaats van zijn id.
     *
     * @param review de om te zetten review
     * @return de response-representatie van de review
     * @throws ItemNotFoundException als de auteur van de review niet meer bestaat
     */
    public ReviewResponse toResponse(Review review) {

        String userName = userService.getFullName(review.getUserId());

        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                userName
        );
    }


}

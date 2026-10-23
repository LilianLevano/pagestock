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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;
    private final ReservationService reservationService;

    public ReviewService(ReviewRepository reviewRepository, BookService bookService, UserService userService, ReservationService reservationService) {
        this.reviewRepository = reviewRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.reservationService = reservationService;
    }

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

    public List<ReviewResponse>  findByBookIsbn(String bookIsbn) {
        if (bookService.findById(bookIsbn).isEmpty()) {
            throw new ItemNotFoundException("Book " + bookIsbn + " not found.");
        }

        return reviewRepository.findByBookIsbnOrderByCreatedAtDesc(bookIsbn).stream().map(this::toResponse).toList();
    }

    public void deleteReview(Long id, String email, boolean isAdmin) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->  new ItemNotFoundException("Review " + id + " not found"));

        User user = userService.findByEmail(email).orElseThrow(() ->  new ItemNotFoundException("User " + email + " not found"));

        if ( !isAdmin && !user.getId().equals(review.getUserId())){
            throw new ReviewDeleteNotAllowedException("You cannot delete the review that you don't own.");
        }

        reviewRepository.deleteById(id);
    }

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

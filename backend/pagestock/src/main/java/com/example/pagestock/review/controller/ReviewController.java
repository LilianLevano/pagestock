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

@RestController
@RequestMapping("/books")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{isbn}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable String isbn) {
        return ResponseEntity.ok(reviewService.findByBookIsbn(isbn));
    }

    @PostMapping("/{isbn}/reviews")
    public ResponseEntity<ReviewResponse> create(@PathVariable String isbn,
                                                 @Valid @RequestBody CreateReviewRequest request,
                                                 Authentication authentication) {
        String email = authentication.getName();
        ReviewResponse response = reviewService.create(isbn, email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        reviewService.deleteReview(id, authentication.getName(), isAdmin);

        return ResponseEntity.noContent().build();
    }
}
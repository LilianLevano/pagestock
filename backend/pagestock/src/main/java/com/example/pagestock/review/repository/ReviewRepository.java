package com.example.pagestock.review.repository;

import com.example.pagestock.reservation.model.ReservationStatus;
import com.example.pagestock.review.model.Review;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ReviewRepository extends ListCrudRepository<Review, Long> {
    List<Review> findByBookIsbnOrderByCreatedAtDesc(String bookIsbn);

    boolean existsByBookIsbnAndUserId(String bookIsbn, Long userId);

}

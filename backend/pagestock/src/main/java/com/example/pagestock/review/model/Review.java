package com.example.pagestock.review.model;

import com.example.pagestock.catalog.model.Book;
import com.example.pagestock.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"book_isbn", "user_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String comment;

    @NotNull(message = "Rating can not be null")
    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank(message = "A review must be bound to a book.")
    private String bookIsbn;

    @NotNull(message = "A review must be bound")
    private Long userId;

    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(String comment, int rating, String bookIsbn, Long userId, LocalDateTime createdAt) {
        this.comment = comment;
        this.rating = rating;
        this.bookIsbn = bookIsbn;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

}

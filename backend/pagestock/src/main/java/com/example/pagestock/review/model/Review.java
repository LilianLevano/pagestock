package com.example.pagestock.review.model;

import com.example.pagestock.catalog.model.Book;
import com.example.pagestock.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title can not be empty.")
    private String title;
    @NotBlank(message = "Description can not be empty.")
    private String description;

    @NotNull(message = "Rating can not be null")
    @Min(1)
    @Max(5)
    private int rating;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book reviewed_book;

    @ManyToOne
    private User user;

    public Review() {
    }

    public Review(String title, String description, int rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Book getReviewed_book() {
        return reviewed_book;
    }

    public void setReviewed_book(Book reviewed_book) {
        this.reviewed_book = reviewed_book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

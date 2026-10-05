package com.example.pagestock.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title can not be empty.")
    private String title;
    @NotBlank(message = "Description can not be empty.")
    private String description;

    @NotNull(message = "Rating can not be null")
    @Size(min=1, max=5)
    private int rating;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book reviewed_book;

    // private User user


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
}

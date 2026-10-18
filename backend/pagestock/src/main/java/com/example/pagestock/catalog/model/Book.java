package com.example.pagestock.catalog.model;

import com.example.pagestock.review.model.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Book {

    @Id
    private String isbnNumber;

    @NotBlank(message = "Title can not be empty.")
    private String title;

    @NotBlank(message = "Description can not be empty.")
    private String description;

    @NotNull(message = "Price can not be null.")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name="author_id")
    )
    private Set<Author> authors;

    @OneToMany(mappedBy = "reviewed_book")
    private List<Review> reviews;


    @NotNull(message = "Publication date can not be null.")
    private LocalDate publicationDate;

    @NotNull(message = "Stock can not be null.")
    private Long stock;

    public Book() {
    }

    public Book(String ISBN_number, String title, String description, BigDecimal price, LocalDate publicationDate, Long stock) {
        this.isbnNumber = ISBN_number;
        this.title = title;
        this.description = description;
        this.price = price;
        this.publicationDate = publicationDate;
        this.stock = stock;
    }

    public String getIsbnNumber() {
        return isbnNumber;
    }

    public void setIsbnNumber(String ISBN_number) {
        this.isbnNumber = ISBN_number;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}

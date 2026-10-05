package com.example.pagestock.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Book {

    @Id
    private Long ISBN_number;

    @NotBlank(message = "Title can not be empty.")
    private String title;

    @NotBlank(message = "Description can not be empty.")
    private String description;

    @NotNull(message = "Price can not be null.")
    private float price;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(mappedBy = "books")
    private List<Author> authors;

    // private Review review

    // private ?list? orders

    @NotNull(message = "Publication date can not be null.")
    private LocalDate publicationDate;

    @NotNull(message = "Stock can not be null.")
    private Long stock;

    public Book() {
    }

    public Book(Long ISBN_number, String title, String description, float price, LocalDate publicationDate, Long stock) {
        this.ISBN_number = ISBN_number;
        this.title = title;
        this.description = description;
        this.price = price;
        this.publicationDate = publicationDate;
        this.stock = stock;
    }

    public Long getISBN_number() {
        return ISBN_number;
    }

    public void setISBN_number(Long ISBN_number) {
        this.ISBN_number = ISBN_number;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}

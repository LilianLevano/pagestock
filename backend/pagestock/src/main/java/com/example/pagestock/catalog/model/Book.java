package com.example.pagestock.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Een boek uit de catalogus van PageStock.
 * <p>
 * Wordt opgeslagen in de tabel {@code books}, met het ISBN-nummer als primaire sleutel.
 * Elk boek hoort bij precies één {@link Category} en kan door meerdere auteurs
 * ({@link Author}) geschreven zijn, gekoppeld via de tabel {@code book_authors}.
 */
@Entity
@Table(name = "books")
public class Book {

    /** ISBN-nummer van het boek; dient als primaire sleutel en wordt niet automatisch gegenereerd. */
    @Id
    private String isbnNumber;

    /** Titel van het boek; mag niet leeg zijn. */
    @NotBlank(message = "Title can not be empty.")
    private String title;

    /** Beschrijving of samenvatting van het boek; mag niet leeg zijn. */
    @NotBlank(message = "Description can not be empty.")
    private String description;

    /** Verkoopprijs van het boek; verplicht. */
    @NotNull(message = "Price can not be null.")
    private BigDecimal price;

    /**
     * Categorie waartoe het boek behoort.
     * <p>
     * Verplicht op databaseniveau (kolom {@code category_id} is {@code NOT NULL}),
     * maar wordt niet door Bean Validation gecontroleerd.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category can not be null.")
    private Category category;

    /** Auteurs van het boek, gekoppeld via de tabel {@code book_authors}. */
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name="author_id")
    )
    private Set<Author> authors;

    /** Datum waarop het boek is gepubliceerd; verplicht. */
    @NotNull(message = "Publication date can not be null.")
    private LocalDate publicationDate;

    /** Aantal exemplaren dat op voorraad is; verplicht. */
    @NotNull(message = "Stock can not be null.")
    private Long stock;

    /** Lege constructor, vereist door JPA. */
    public Book() {
    }

    /**
     * Maakt een boek aan zonder categorie en auteurs; die worden apart ingesteld
     * via {@link #setCategory(Category)} en {@link #setAuthors(Set)}.
     *
     * @param ISBN_number     ISBN-nummer, gebruikt als primaire sleutel
     * @param title           titel van het boek
     * @param description     beschrijving van het boek
     * @param price           verkoopprijs van het boek
     * @param publicationDate publicatiedatum van het boek
     * @param stock           aantal exemplaren op voorraad
     */
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

}

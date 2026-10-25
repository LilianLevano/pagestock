package com.example.pagestock.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Een categorie waarin boeken uit de catalogus worden ingedeeld.
 * <p>
 * Wordt opgeslagen in de tabel {@code categories}. Elke categorie heeft een unieke naam
 * en kan meerdere boeken ({@link Book}) bevatten.
 */
@Entity
@Table(name= "categories")
public class Category {
    /** Unieke identificatie van de categorie; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Naam van de categorie; mag niet leeg zijn en moet uniek zijn in de database. */
    @NotBlank(message = "Name can not be empty.")
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Boeken die tot deze categorie behoren.
     * <p>
     * Inverse kant van {@link Book#getCategory()}: de koppeling wordt opgeslagen via
     * het boek (kolom {@code category_id}), niet via deze lijst.
     */
    @OneToMany(mappedBy = "category")
    private List<Book> books;

    /** Lege constructor, vereist door JPA. */
    public Category() {
    }

    /**
     * Maakt een nieuwe categorie aan; het id wordt bij het opslaan door de database toegekend.
     *
     * @param name unieke naam van de categorie
     */
    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}

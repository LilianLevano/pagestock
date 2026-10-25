package com.example.pagestock.catalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * Een auteur van een of meer boeken uit de catalogus.
 * <p>
 * Wordt opgeslagen in de tabel {@code authors}. De koppeling met {@link Book} is een
 * many-to-many relatie die door {@link Book} wordt beheerd (koppeltabel {@code book_authors}).
 */
@Entity
@Table(name = "authors")
public class Author {
    /** Unieke identificatie van de auteur; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Naam van de auteur; mag niet leeg zijn. */
    @NotBlank(message = "Name can not be empty")
    private String name;

    /**
     * Boeken van deze auteur.
     * <p>
     * Inverse kant van {@link Book#getAuthors()}: wijzigingen moeten via het boek gebeuren
     * om in de database te worden opgeslagen.
     */
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;

    /** Lege constructor, vereist door JPA. */
    public Author() {
    }

    /**
     * Maakt een nieuwe auteur aan; het id wordt bij het opslaan door de database toegekend.
     *
     * @param name naam van de auteur
     */
    public Author(String name) {
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

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}

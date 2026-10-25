package com.example.pagestock.user.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Een gebruiker van PageStock (klant of beheerder).
 * <p>
 * Wordt opgeslagen in de tabel {@code users}. Het e-mailadres dient als gebruikersnaam bij het
 * inloggen en wordt altijd in kleine letters opgeslagen.
 */
@Entity
@Table(name = "users")
public class User {

    /** Unieke identificatie van de gebruiker; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Voornaam; mag niet leeg zijn, maximaal 50 tekens. */
    @NotBlank(message = "First name can not be empty")
    @Size(max = 50)
    private String firstName;

    /** Achternaam; mag niet leeg zijn, maximaal 50 tekens. */
    @NotBlank(message = "Last name can not be empty")
    @Size(max = 50)
    private String lastName;

    /** E-mailadres, gebruikt om in te loggen; uniek, in kleine letters. */
    @NotBlank(message = "Email can not be empty")
    @Column(unique = true, nullable = false)
    @Email(message = "Email is not valid")
    private String email;

    /** BCrypt-hash van het wachtwoord; het wachtwoord zelf wordt nooit opgeslagen. */
    @NotBlank(message = "Password can not be empty")
    private String password;

    /** Telefoonnummer; verplicht en uniek. */
    @NotBlank(message = "Phone number can not be empty")
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    /**
     * Rol van de gebruiker; standaard {@link Role#USER}. Wordt als tekst opgeslagen
     * (bv. {@code "ADMIN"}). Beheerders worden niet via registratie aangemaakt, maar door de
     * {@code DataSeeder}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /** Lege constructor, vereist door JPA. */
    public User() {
    }

    /**
     * Maakt een gebruiker aan met de rol {@link Role#USER}.
     *
     * @param firstName   voornaam
     * @param lastName    achternaam
     * @param email       e-mailadres
     * @param password    het al gehashte wachtwoord (BCrypt), niet het wachtwoord in platte tekst
     * @param phoneNumber telefoonnummer
     */
    public User(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

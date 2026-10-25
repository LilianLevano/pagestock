package com.example.pagestock.config;

import com.example.pagestock.catalog.model.Author;
import com.example.pagestock.catalog.model.Book;
import com.example.pagestock.catalog.model.Category;
import com.example.pagestock.catalog.repository.AuthorRepository;
import com.example.pagestock.catalog.repository.BookRepository;
import com.example.pagestock.catalog.repository.CategoryRepository;
import com.example.pagestock.user.model.Role;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

// Vult de databank met testdata bij het opstarten van de applicatie
/**
 * Vult de database met testdata bij het opstarten van de applicatie.
 * <p>
 * Maakt twee testaccounts aan als ze nog niet bestaan: {@code user@gmail.com} (rol {@code USER})
 * en {@code admin@gmail.com} (rol {@code ADMIN}). Dit is de enige manier om een beheerder aan te
 * maken. Daarnaast worden 5 categorieën, 12 auteurs en 8 boeken aangemaakt, maar alleen als er
 * nog geen enkel boek in de database staat. Zo ontstaan er geen dubbele gegevens bij een herstart.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Maakt de seeder aan met de benodigde afhankelijkheden.
     *
     * @param bookRepository     repository om boeken op te slaan
     * @param authorRepository   repository om auteurs op te slaan
     * @param categoryRepository repository om categorieën op te slaan
     * @param userRepository     repository om de testaccounts op te slaan
     * @param passwordEncoder    BCrypt-encoder om de wachtwoorden van de testaccounts te hashen
     */
    @Autowired
    public DataSeeder(BookRepository bookRepository, AuthorRepository authorRepository,
                      CategoryRepository categoryRepository, UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Maakt de testaccounts en, als de database nog geen boeken bevat, de testcatalogus aan.
     *
     * @param args command-line argumenten van de applicatie (niet gebruikt)
     */
    @Override
    public void run(String... args) {
        seedUser("Test", "User", "user@gmail.com", "user1234", "0470000001", Role.USER);
        seedUser("Test", "Admin", "admin@gmail.com", "admin1234", "0470000002", Role.ADMIN);
        // Er zijn al boeken: niets doen, anders krijgen we dubbele data
        if (bookRepository.count() > 0) {
            return;
        }

        // Eerst categorieën en auteurs opslaan: Book heeft geen cascade,
        // dus ze moeten al een id hebben voor we ze aan een boek koppelen
        Category fiction = categoryRepository.save(new Category("Fiction"));
        Category fantasy = categoryRepository.save(new Category("Fantasy"));
        Category sciFi = categoryRepository.save(new Category("Science Fiction"));
        Category classics = categoryRepository.save(new Category("Classics"));
        Category computing = categoryRepository.save(new Category("Computer Science"));

        Author orwell = authorRepository.save(new Author("George Orwell"));
        Author rowling = authorRepository.save(new Author("J.K. Rowling"));
        Author tolkien = authorRepository.save(new Author("J.R.R. Tolkien"));
        Author herbert = authorRepository.save(new Author("Frank Herbert"));
        Author austen = authorRepository.save(new Author("Jane Austen"));
        Author pratchett = authorRepository.save(new Author("Terry Pratchett"));
        Author gaiman = authorRepository.save(new Author("Neil Gaiman"));
        Author martin = authorRepository.save(new Author("Robert C. Martin"));
        Author gamma = authorRepository.save(new Author("Erich Gamma"));
        Author helm = authorRepository.save(new Author("Richard Helm"));
        Author johnson = authorRepository.save(new Author("Ralph Johnson"));
        Author vlissides = authorRepository.save(new Author("John Vlissides"));

        // Daarna de boeken, in één keer opgeslagen met saveAll
        bookRepository.saveAll(List.of(
                book("9780451524935", "1984", "A dystopian novel about Big Brother and total surveillance.",
                        "9.99", LocalDate.of(1949, 6, 8), 10L, classics, Set.of(orwell)),
                book("9780747532699", "Harry Potter and the Philosopher's Stone", "A boy discovers he is a wizard.",
                        "12.50", LocalDate.of(1997, 6, 26), 5L, fantasy, Set.of(rowling)),
                book("9780547928227", "The Hobbit", "Bilbo Baggins joins a quest to reclaim a dwarf kingdom.",
                        "11.00", LocalDate.of(1937, 9, 21), 3L, fantasy, Set.of(tolkien)),
                book("9780441172719", "Dune", "Politics, religion and ecology on the desert planet Arrakis.",
                        "14.99", LocalDate.of(1965, 8, 1), 2L, sciFi, Set.of(herbert)),
                book("9780141439518", "Pride and Prejudice", "Elizabeth Bennet and Mr Darcy in Regency England.",
                        "7.99", LocalDate.of(1813, 1, 28), 0L, classics, Set.of(austen)),
                book("9780060853983", "Good Omens", "An angel and a demon try to stop the apocalypse.",
                        "10.99", LocalDate.of(1990, 5, 1), 4L, fiction, Set.of(pratchett, gaiman)),
                book("9780132350884", "Clean Code", "A handbook of agile software craftsmanship.",
                        "39.99", LocalDate.of(2008, 8, 1), 6L, computing, Set.of(martin)),
                book("9780201633610", "Design Patterns", "The classic catalogue of 23 object-oriented design patterns.",
                        "49.99", LocalDate.of(1994, 10, 31), 1L, computing, Set.of(gamma, helm, johnson, vlissides))
        ));
    }

    /**
     * Maakt een gebruiker aan, tenzij er al een gebruiker met dit e-mailadres bestaat.
     *
     * @param firstName   voornaam
     * @param lastName    achternaam
     * @param email       e-mailadres, in kleine letters
     * @param rawPassword wachtwoord in platte tekst; wordt hier met BCrypt gehasht
     * @param phoneNumber telefoonnummer
     * @param role        rol van de gebruiker
     */
    private void seedUser(String firstName, String lastName, String email, String rawPassword,
                          String phoneNumber, Role role) {
        // Gebruiker bestaat al: niets doen (e-mail is uniek in de databank)
        if (userRepository.existsByEmail(email)) {
            return;
        }
        // Wachtwoord hashen met BCrypt, net zoals bij register; anders werkt login niet
        User user = new User(firstName, lastName, email, passwordEncoder.encode(rawPassword), phoneNumber);
        // Rol expliciet zetten (standaard is USER)
        user.setRole(role);
        userRepository.save(user);
    }

    /**
     * Maakt een boek aan (zonder het op te slaan) en koppelt de categorie en de auteurs.
     *
     * @param isbn        ISBN-nummer
     * @param title       titel
     * @param description beschrijving
     * @param price       prijs als tekst (bv. {@code "9.99"}), om afrondingsfouten van {@code double} te vermijden
     * @param published   publicatiedatum
     * @param stock       aantal exemplaren op voorraad
     * @param category    een al opgeslagen categorie
     * @param authors     al opgeslagen auteurs
     * @return het nieuwe, nog niet opgeslagen boek
     */
    private Book book(String isbn, String title, String description, String price, LocalDate published,
                      Long stock, Category category, Set<Author> authors) {
        // BigDecimal uit een String, zodat er geen afrondingsfouten van double in komen
        Book book = new Book(isbn, title, description, new BigDecimal(price), published, stock);
        book.setCategory(category);
        book.setAuthors(authors);
        return book;
    }
}
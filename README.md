# PageStock

Lilian Levano

Erasmushogeschool Brussel

Tweedejaar Graduaat in het Programmeren

## 1. Omschrijving

PageStock is een fictieve boekhandel waar iedereen informatie over boeken kan bekijken. Ingelogde gebruikers kunnen boeken reserveren, hun reservaties opvolgen en een review plaatsen over een boek dat ze hebben afgehaald. Een admin beheert de catalogus en bevestigt de afhalingen, waarbij de voorraad automatisch wordt aangepast.

Het project bestaat uit een Spring Boot-backend (REST API) en een Laravel/Blade-frontend, en werd gemaakt in het kader van de examenopdracht voor het vak "Application Architecture".

### Functionaliteiten
- [x] Catalogus bekijken: lijst van boeken, zoeken op titel, detailpagina met auteurs, categorie, prijs en voorraad
- [x] CRUD boeken, auteurs en categorieën (alleen admin)
- [x] Registreren en inloggen (Spring Security, JWT)
- [x] Rollen `USER` en `ADMIN`: alleen een admin mag de catalogus wijzigen en afhalingen bevestigen
- [x] Winkelmandje en reservatie van een of meerdere boeken
- [x] Eigen reservaties bekijken en annuleren zolang ze niet afgehaald zijn
- [x] Admin bevestigt de afhaling: de voorraad van elk boek wordt verlaagd
- [x] Overzicht van de gekochte (afgehaalde) boeken in het profiel ("Mijn boeken")
- [x] Reviews (score van 1 tot 5 en tekst) op de detailpagina van een boek, alleen voor wie het boek heeft afgehaald, één review per boek
  

## 2. Architectuur

```
Browser --> Laravel-frontend (Blade) --HTTP/JSON--> Spring Boot REST API --> MySQL
```
 
De repository bevat twee afzonderlijke applicaties:
 
- `backend/pagestock`: Spring Boot REST API (Java 17, Maven)
- `frontend/pagestock`: Laravel-applicatie die de API aanroept (URL ingesteld via `API_URL` in `.env`)


De frontend heeft geen eigen data over boeken of gebruikers: alles komt uit de API. Na het inloggen bewaart Laravel het JWT-token en de rol in de sessie en stuurt het token mee bij elke beveiligde request. Het winkelmandje staat ook in de sessie, tot de gebruiker het omzet in een reservatie.

### Opbouw van de backend

Gelaagde architectuur, opgedeeld in packages per domein: `catalog` (boeken, auteurs, categorieën), `user`, `reservation` en `review`. Elk domein bevat zijn eigen lagen:

- `controller`: stelt de REST-routes beschikbaar en zet resultaten om naar HTTP-statuscodes
- `service`: bedrijfslogica (bv. `BookService`)
- `repository`: toegang tot de data
- `model`: JPA-entiteiten
- `dto`: in- en uitvoerobjecten (`record`), zodat entiteiten nooit blootgesteld worden

Daarnaast bevat het package `exceptions` de eigen excepties die door alle domeinen gedeeld worden (`ItemNotFoundException`, `BookAlreadyExistsException`, …) en het package `config` de klasse `DataSeeder`, die bij het opstarten testgegevens aanmaakt.

De beveiliging staat in twee packages buiten de domeinen, omdat ze voor alle domeinen geldt:

- `config`: `SecurityConfig` bepaalt welke routes publiek zijn, welke een login vragen en welke de rol `ADMIN` vereisen, en levert de beans `PasswordEncoder` (BCrypt) en `AuthenticationManager`.
- `security`: `JwtService` maakt en controleert de tokens, `JwtAuthenticationFilter` leest bij elke request de header `Authorization: Bearer <token>` en laat Spring weten wie ingelogd is.

Domeinen zijn van elkaar gescheiden: er bestaat geen JPA-relatie tussen entiteiten van verschillende domeinen. Een domein verwijst naar een ander domein enkel via een id (bv. `userId`, `bookIsbn`) en roept indien nodig de service van dat domein aan, nooit rechtstreeks zijn repository. Zo kan elk domein afzonderlijk gelezen, getest en aangepast worden.

- `ReservationService` vraagt aan `BookService` of een boek bestaat en genoeg voorraad heeft, en laat `BookService.decreaseStock` de voorraad verlagen bij een afhaling. Alleen het domein `catalog` wijzigt de voorraad.
- `ReviewService` vraagt aan `ReservationService.hasPickedUp` of de gebruiker het boek heeft afgehaald voor hij een review mag plaatsen.

### Opbouw van de frontend
 
- `app/Http/Controllers`: één controller per functionaliteit (`CatalogController`, `AuthController`, `CartController`, `ReservationController`, `ReviewController`) en aparte controllers voor het adminpaneel (`AdminBookController`, `AdminAuthorController`, `AdminCategoryController`, `AdminReservationController`). Ze roepen de API aan met de HTTP-client van Laravel.
- `app/Http/Middleware`: `EnsureApiToken` laat alleen ingelogde gebruikers door, `EnsureAdmin` alleen admins.
- `app/View/Components` en `resources/views/components`: herbruikbare Blade-componenten (layout, navigatie, boekkaart, flashberichten, status van een reservatie).
- `resources/views`: de pagina's, gegroepeerd per onderdeel (`books`, `auth`, `reservations`, `reviews`, `admin`, `errors`).


### Gebruikte patronen
 
- **MVC**: model = entiteiten en DTO's; controllers = `controller`-klassen in de API en Laravel-controllers in de frontend; view = Blade-templates (de JSON-respons van de API vervult de rol van view in de backend).
- **DAO**: de interfaces `BookRepository`, `CategoryRepository`, `UserRepository`, … breiden `ListCrudRepository` uit en isoleren de toegang tot de database (bv. `existsByCategoryId`, `findByTitleContainingIgnoreCase`, `findByEmail`).
- **IoC / dependency injection**: Spring maakt de beans aan en injecteert ze via de constructor (bv. `BookService` ontvangt `BookRepository` en `CategoryRepository`, `UserService` ontvangt de `PasswordEncoder` die in `SecurityConfig` als `@Bean` gedefinieerd is).

### Technische keuzes

- DTO's als `record`: entiteiten verlaten de API nooit. `UserResponse` bevat nooit het wachtwoord.
- ISBN als `String` als identificatie van het boek.
- `BigDecimal` voor de prijs, om afrondingsfouten van `double` te vermijden.
- Eigen excepties in plaats van generieke, om precieze HTTP-statuscodes te kunnen teruggeven (400, 404, 409).
-  `@Transactional` op de methodes van `ReservationService`: als één lijn van een afhaling faalt (bv. te weinig voorraad), wordt niets van de afhaling opgeslagen.
- Een reservatie verlaagt de voorraad nog niet: dat gebeurt pas bij de bevestiging van de afhaling door een admin. Bij de afhaling wordt de voorraad opnieuw gecontroleerd.
- Wachtwoorden worden gehasht met BCrypt (eenrichtingshash met salt), nooit in klare tekst opgeslagen.
- JWT in plaats van sessies: de API is stateless. Het token is ondertekend met een geheime sleutel die alleen de server kent, zodat een client het niet kan vervalsen. Een token is 24 uur geldig.
- Een nieuwe gebruiker krijgt altijd de rol `USER`; de rol kan niet via de registratie gekozen worden.

## 3. Vereisten
 
- Java 17
- Maven (of de meegeleverde wrapper `mvnw`)
- MySQL
- PHP 8.4 en Composer
- Node.js en npm (voor Vite en Tailwind CSS)
  
## 4. Installatie en opstarten
Eerst en vooral, clone deze repository in een folder.

```bash
git clone https://github.com/LilianLevano/pagestock
```

Start eerst de backend, daarna de frontend.
 
### Backend
 
1. Maak een lege MySQL-database aan: `CREATE DATABASE pagestock;`
2. Kopieer in `backend/pagestock/src/main/resources/` het bestand `application.properties.example` naar `application.properties`.

Powershell: 

```bash
cp application.properties.example application.properties
```
```powershell
notepad application.properties
``` 

En vul de MySQL-gebruiker en het wachtwoord in, evenals het JWT-geheim:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=jouw-wachtwoord
   jwt.secret=een-lange-geheime-zin-van-minstens-32-tekens
   ```
Wees zeker dat "spring.datasource.url=" naar het juist Database adres wijst.
Het geheim moet minstens 32 tekens lang zijn (256 bits), anders start de applicatie niet.

3. Starten:
   ```bash
   cd backend/pagestock
   mvn spring-boot:run
   ```

De tabellen worden automatisch aangemaakt (`ddl-auto=update`). De API is bereikbaar op `http://localhost:8080`.

### Testgegevens
 
Bij het opstarten maakt `DataSeeder` automatisch twee accounts aan, en enkele categorieën, auteurs en boeken als de catalogus nog leeg is:
 
| Rol | E-mail | Wachtwoord |
|---|---|---|
| `USER` | `user@gmail.com` | `user1234` |
| `ADMIN` | `admin@gmail.com` | `admin1234` |
 
Een andere gebruiker admin maken kan rechtstreeks in MySQL:
 
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'jouw@email.com';
```

### Frontend

Houdt de terminal die de backend draait open, anders sluit de API.
 
```bash
cd frontend/pagestock
cp .env.example .env
```

```bash
notepad .env
```
Vul in .env API_URL in (bv. http://localhost:8080)

```bash
composer install //kan redelijk veel tijd nemen als het de eerste keer is
php artisan key:generate
php artisan migrate
```
Na artisan migrate, gaat de console je vragen om een SQLite database file te genereren. Typ "yes".

De SQLite-database van Laravel bevat geen boeken of gebruikers: ze dient alleen om de sessies op te slaan (`SESSION_DRIVER=database`).

Nu, start de applicatie met:
```bash
php artisan serve
```

De applicatie is bereikbaar op `http://127.0.0.1:8000`. (Kan anders zijn, check het gegeven adres in de console.)  


## 5. Documentatie van de endpoints

### Authenticatie

1. Registreer met `POST /auth/register`.
2. Log in met `POST /auth/login`: het antwoord bevat een token met de role van de ingelogde user (ADMIN of USER).
3. Stuur dat token mee in de header van elke beveiligde request:
```
Authorization: Bearer <token>
```

Betekenis van de kolom **Toegang**: *publiek* = geen token nodig; *user* = ingelogd (elke rol); *admin* = ingelogd met de rol `ADMIN`. Zonder geldig token geeft een beveiligde route **401**, met een te lage rol **403**. Bij ongeldige velden (validatie) geven `POST`- en `PUT`-routes **400** met de foutmeldingen per veld.

| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| POST | `/auth/register` | publiek | Maakt een account aan (rol `USER`) | 201, 400 bij ongeldige velden of verschillende wachtwoorden, 409 als het e-mailadres al bestaat |
| POST | `/auth/login` | publiek | Geeft een JWT en de rol terug | 200, 400, 401 bij fout e-mailadres of wachtwoord |

Voorbeeld van een body voor `POST /auth/register`:

```json
{
  "firstName": "Lilian",
  "lastName": "Levano",
  "email": "test@gmail.com",
  "password": "testpassword",
  "confirmPassword": "testpassword",
  "phoneNumber": "0470123456"
}
```

Voorbeeld van een body voor `POST /auth/login` en het antwoord:

```json
{ "email": "test@gmail.com", "password": "testpassword" }
```
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "role": "USER" }
```

### Catalogus
 
Het id van een boek is zijn ISBN (10 of 13 tekens).
 
| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| GET | `/books` | publiek | Geeft alle boeken terug | 200 |
| GET | `/books/{isbn}` | publiek | Detail van een boek | 200, 404 |
| GET | `/books/search?title=...` | publiek | Zoeken op titel (hoofdletterongevoelig) | 200 |
| POST | `/books` | admin | Maakt een boek aan | 201, 400, 404 als de categorie of een auteur onbekend is, 409 als het ISBN al bestaat |
| PUT | `/books/{isbn}` | admin | Wijzigt een boek (het ISBN zelf kan niet gewijzigd worden) | 200, 400, 404 |
| DELETE | `/books/{isbn}` | admin | Verwijdert een boek | 204, 404 |
 
Voorbeeld van een body voor `POST /books` (voor `PUT` dezelfde velden zonder `isbnNumber`):
 
```json
{
  "isbnNumber": "9780132350884",
  "title": "Clean Code",
  "description": "A handbook of agile software craftsmanship.",
  "price": 39.99,
  "publicationDate": "2008-08-01",
  "stock": 5,
  "categoryId": 1,
  "authorIds": [1]
}
```

Antwoord (`BookResponse`): dezelfde velden, met `category` (`id`, `name`) en `authors` (lijst van `id`, `name`) in plaats van `categoryId` en `authorIds`.

### Auteurs en categorieën
 
`/authors` en `/categories` hebben dezelfde routes. De body bevat enkel een naam: `{ "name": "Robert C. Martin" }`. Het antwoord bevat `id` en `name`.
 
| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| GET | `/authors` | publiek | Geeft alle auteurs terug | 200 |
| GET | `/authors/{id}` | publiek | Detail van een auteur | 200, 404 |
| GET | `/authors/search?name=...` | publiek | Zoeken op naam | 200 |
| POST | `/authors` | admin | Maakt een auteur aan | 201, 400, 409 als de naam al bestaat |
| PUT | `/authors/{id}` | admin | Wijzigt de naam | 200, 400, 404, 409 als de naam al bestaat |
| DELETE | `/authors/{id}` | admin | Verwijdert een auteur | 204, 404, 409 als er nog boeken aan gekoppeld zijn |
| GET | `/categories` | publiek | Geeft alle categorieën terug | 200 |
| GET | `/categories/{id}` | publiek | Detail van een categorie | 200, 404 |
| GET | `/categories/search?name=...` | publiek | Zoeken op naam | 200 |
| POST | `/categories` | admin | Maakt een categorie aan | 201, 400, 409 als de naam al bestaat |
| PUT | `/categories/{id}` | admin | Wijzigt de naam | 200, 400, 404, 409 als de naam al bestaat |
| DELETE | `/categories/{id}` | admin | Verwijdert een categorie | 204, 404, 409 als er nog boeken aan gekoppeld zijn |

### Reservaties
 
Een reservatie heeft een status: `RESERVED` (in afwachting), `PICKED_UP` (afgehaald) of `CANCELLED` (geannuleerd). De gebruiker wordt bepaald via het token, niet via de body.
 
| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| POST | `/reservations` | user | Maakt een reservatie met status `RESERVED` | 201, 400 als een boek twee keer voorkomt, 404 als een boek onbekend is, 409 als de lijst leeg is |
| GET | `/reservations/me` | user | Alle reservaties van de ingelogde gebruiker | 200 |
| GET | `/reservations/me/purchased` | user | Afgehaalde reservaties van de gebruiker (gekochte boeken) | 200 |
| PATCH | `/reservations/{id}/cancel` | user | Annuleert een eigen reservatie of wordt door een admin geannuleert | 200, 403 als de reservatie niet meer `RESERVED` is, 404 |
| GET | `/reservations` | admin | Alle reservaties die nog afgehaald moeten worden | 200 |
| PATCH | `/reservations/{id}/pickup` | admin | Bevestigt de afhaling: status `PICKED_UP` en voorraad verlaagd | 200, 403 als de reservatie niet meer `RESERVED` is, 404, 409 als de voorraad intussen te laag is |
 
Voorbeeld van een body voor `POST /reservations` en het antwoord:
```json
{
  "lines": [
    { "bookIsbn": "9780132350884", "quantity": 2 },
    { "bookIsbn": "9780451524935", "quantity": 1 }
  ]
}
```
```json
{
  "id": 1,
  "status": "RESERVED",
  "reservedAt": "2026-10-24T14:30:00",
  "pickedUpAt": null,
  "lines": [
    { "bookIsbn": "9780132350884", "quantity": 2, "bookTitle": "Clean Code" },
    { "bookIsbn": "9780451524935", "quantity": 1, "bookTitle": "1984" }
  ]
}
```

### Reviews
 
| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| GET | `/books/{isbn}/reviews` | publiek | Reviews van een boek, nieuwste eerst | 200, 404 als het boek onbekend is |
| POST | `/books/{isbn}/reviews` | user | Plaatst een review | 201, 400, 403 als de gebruiker het boek niet heeft afgehaald, 404, 409 bij een tweede review op hetzelfde boek |
| DELETE | `/books/reviews/{id}` | user | Verwijdert een review (eigen review, of elke review als admin) | 204, 403 als het niet je eigen review is, 404 |
 
Voorbeeld van een body voor `POST /books/{isbn}/reviews` en het antwoord:
 
```json
{ "rating": 4, "comment": "Heel leerrijk boek" }
```
```json
{
  "id": 1,
  "rating": 4,
  "comment": "Heel leerrijk boek",
  "createdAt": "2026-10-24T15:00:00",
  "authorReviewName": "Test User"
}
```

## 6. Tests

### Unit tests (backend)
 
```bash
cd backend/pagestock
./mvnw test
```
 
| Test | Wat wordt gecontroleerd | Waarom kritiek |
|---|---|---|
| `BookServiceTest.decreaseStock_voldoendeVoorraad_verlaagtVoorraad` | Bij een afhaling van 2 exemplaren van een boek met voorraad 5 wordt de voorraad 3 | De voorraad bijwerken is de kern van de afhaling: een fout hier geeft een verkeerde voorraad in de winkel |
| `BookServiceTest.decreaseStock_teWeinigVoorraad_gooitOutOfStockException` | Met voorraad 1 geeft een afhaling van 2 exemplaren een `OutOfStockException` en blijft de voorraad 1 | De voorraad mag nooit negatief worden |
 
`BookServiceTest` gebruikt Mockito: de `BookRepository` wordt vervangen door een mock, zodat de test geen database nodig heeft en alleen de logica van `BookService` test. `contextLoads` start de volledige applicatie en heeft dus een werkende MySQL-database en een ingevuld `application.properties` nodig.
 
### Functionele tests
 
De functionele scenario's (in Gherkin) staan in [TESTS.md](TESTS.md): een afhaling bevestigen als admin (met de controle van de voorraad), een boek toevoegen aan het winkelmandje en een review schrijven. Ze werden manueel uitgevoerd in de browser met de testaccounts van de `DataSeeder`.
 
De API werd daarnaast manueel getest met Postman (bv. `POST /authors` zonder token geeft 401, met een token van een `USER` 403 en met een token van een `ADMIN` 201).
 

## 7. Documentatie van de code
 
De code van beide applicaties is gedocumenteerd met doc comments: JavaDoc in de backend, PhpDoc in de frontend. Elke klasse en elke publieke methode heeft een beschrijving in het Nederlands; technische termen (controller, service, token, …) blijven in het Engels. Uit die comments werd een HTML-documentatie gegenereerd, die mee in de repository staat.

Wat beschreven wordt:

- per klasse: haar rol en, waar nodig, de regels die ze afdwingt (bv. de statussen van een reservatie in `ReservationService`, de toegang per route in de controllers);
- per methode: wat ze doet, de parameters (`@param`), het resultaat (`@return`) en de excepties die ze kan gooien (`@throws`);
- in de controllers van de backend: het endpoint en de mogelijke HTTP-statuscodes;
- in de controllers van de frontend: de Laravel-route, de aangeroepen API-route en de getoonde view.

Korte comments (`//`) in de code verduidelijken enkel de stappen die niet meteen duidelijk zijn.

### Backend (JavaDoc)

De documentatie staat in `backend/pagestock/docs/javadoc/`. Open `index.html` in een browser.

Ze bevat alle packages per domein (`catalog`, `user`, `reservation`, `review`) en de gedeelde packages (`config`, `security`, `exceptions`).

Opnieuw genereren: in IntelliJ via *Tools > Generate JavaDoc...*, met `docs/javadoc` als output-map.

### Frontend (PhpDoc)

De documentatie werd gegenereerd met phpDocumentor (`phpdocumentor/shim`, dev-dependency in `composer.json`) en staat in `frontend/pagestock/docs/phpDoc/`. Open `index.html` in een browser.

Ze bevat de klassen uit de map `app`: de controllers, de middleware (`EnsureApiToken`, `EnsureAdmin`) et de Blade-componenten.

Opnieuw genereren:

```bash
cd frontend/pagestock
vendor/bin/phpdoc -d app -t docs/phpDoc
```
## 8. Planning
 
Het project werd gebouwd in vier fasen. Elke commit begint met het onderdeel (`BACKEND`, `FRONTEND`, `DOCS`), zodat de volledige voortgang in de Git-geschiedenis te volgen is.
 
| Periode | Werk |
|---|---|
| 29/09 – 05/10 | Opzet van de twee projecten (Spring Boot en Laravel), verbinding frontend-backend getest, eerste entiteiten (`Book`, `Author`, `Category`, `Review`) |
| 14/10 – 17/10 | Catalogus in de backend: DTO's, repositories, services, controllers en eigen excepties voor boeken, categorieën en auteurs; eerste versie van de README |
| 18/10 – 20/10 | Opsplitsing in packages per domein, authenticatie (registratie, login, JWT, rollen), reservaties en de afhaling met aanpassing van de voorraad |
| 23/10 – 25/10 | Reviews en `DataSeeder` in de backend, volledige Laravel-frontend (catalogus, login, winkelmandje, reservaties, reviews, adminpaneel), tests en documentatie |
|25/10 - 28/10| Code controle, laatste details rechtzetten |
 

## 9. Bronnen en AI-Tools

### Documentatie en bronnen
 
- Documentatie van Spring Boot en Spring Data JPA (https://docs.spring.io)
- Documentatie van Spring Security (https://docs.spring.io/spring-security/reference/)
- Documentatie van de bibliotheek jjwt (https://github.com/jwtk/jjwt)
- Documentatie van JUnit 5 (https://junit.org/junit5/docs/current/user-guide/) en Mockito (https://site.mockito.org)
- Documentatie van Laravel (https://laravel.com/docs)
- Documentatie van Tailwind CSS (https://tailwindcss.com/docs)

### AI-tools
- **Claude (Anthropic)** werd gebruikt als begeleider tijdens het hele project:
  - advies over de architectuur (opsplitsing per domein, geen JPA-relaties tussen domeinen, gebruik van DTO's) en over technische keuzes;
  - nalezen van mijn code, met verbeteringen en uitleg die ik begrepen heb voor ik ze toepaste;
  - uitleg over concepten (IoC, Spring Data-methodes, transacties, HTTP-statuscodes);
  - **Spring Security en JWT**: dit onderwerp werd niet in de les behandeld. Hier gaf Claude code met uitleg (`SecurityConfig`, `CustomUserDetailsService`, `JwtService`, `JwtAuthenticationFilter`), die ik zelf heb overgetypt en waarover ik controlevragen kreeg. Details staan in [backend/pagestock/docs/journal-auth.md](backend/pagestock/docs/journal-auth.md);
  - **frontend (Laravel/Blade)**: Controllers heb ik grotendeels zelf geschreven, dan door AI laten herlezen om ze beter te maken. Blade paginas (met Tailwind CSS) werden gegenereerd en door mezelf gecontroleerd en aangepast
  - hulp en correctie bij het opstellen van deze README.
- De rest van de code heb ik zelf geschreven.

Links naar de respectieve chats kan ik niet delen aangezien ik "Claude Project" heb gebruikt, waardoor de deling onmogelijk is. (Dit wist ik niet voordat ik het gebruikte)

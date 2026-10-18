# PageStock

Lilian Levano
Erasmushogeschool Brussel
Tweedejaar Graduaat in het Programmeren
## 1. Omschrijving

 Pagestock is een fictieve boekhandel waar gebruikers informatie over boeken kunnen bekijken.
 Ingelogde gebruikers kunnen dan een boek reserveren en een review plaatsen.
 Het project bestaat uit een Spring Boot backend en een Laravel/Blade (Frontend) applicatie werd gemaakt in het kader van de examenopdracht voor het vak "Application Architecture". 

### Functionaliteiten
- [x] CRUD boeken, auteurs, categorieën
- [x] Registreren en inloggen (Spring Security, JWT)
- [x] Rollen `USER` en `ADMIN`: alleen een admin mag de catalogus wijzigen
- ...

## 2. Architecture

```
Browser --> Laravel-frontend (Blade) --> Spring Boot REST API --> MySQL
```
 
De repository bevat twee afzonderlijke applicaties:
 
- `backend/pagestock`: Spring Boot REST API (Java 17, Maven)
- `frontend/pagestock`: Laravel-applicatie die de API aanroept (URL ingesteld via `API_URL` in `.env`)


### Opbouw van de backend

Gelaagde architectuur, opgedeeld in packages per domein: `catalog` (boeken, auteurs, categorieën), `user`, `reservation` en `review`. Elk domein bevat zijn eigen lagen:

- `controller`: stelt de REST-routes beschikbaar en zet resultaten om naar HTTP-statuscodes
- `service`: bedrijfslogica (bv. `BookService`)
- `repository`: toegang tot de data
- `model`: JPA-entiteiten
- `dto`: in- en uitvoerobjecten (`record`), zodat entiteiten nooit blootgesteld worden

Daarnaast bevat het package `exceptions` de eigen excepties die door alle domeinen gedeeld worden (`ItemNotFoundException`, `BookAlreadyExistsException`, …).

De beveiliging staat in twee packages buiten de domeinen, omdat ze voor alle domeinen geldt:

- `config`: `SecurityConfig` bepaalt welke routes publiek zijn, welke een login vragen en welke de rol `ADMIN` vereisen, en levert de beans `PasswordEncoder` (BCrypt) en `AuthenticationManager`.
- `security`: `JwtService` maakt en controleert de tokens, `JwtAuthenticationFilter` leest bij elke request de header `Authorization: Bearer <token>` en laat Spring weten wie ingelogd is.

Domeinen zijn van elkaar gescheiden: er bestaat geen JPA-relatie tussen entiteiten van verschillende domeinen. Een domein verwijst naar een ander domein enkel via een id (bv. `userId`, `bookIsbn`) en roept indien nodig de service van dat domein aan, nooit rechtstreeks zijn repository. Zo kan elk domein afzonderlijk gelezen, getest en aangepast worden.

### Gebruikte patronen
 
- **MVC**: model = entiteiten en DTO's; controllers = `controller`-klassen in de API en Laravel-controllers in de frontend; view = Blade-templates (de JSON-respons van de API vervult de rol van view in de backend).
- **DAO**: de interfaces `BookRepository`, `CategoryRepository`, `UserRepository`, … breiden `ListCRUDRepository` uit en isoleren de toegang tot de database (bv. `existsByCategoryId`, `findByTitleContainingIgnoreCase`, `findByEmail`).
- **IoC / dependency injection**: Spring maakt de beans aan en injecteert ze via de constructor (bv. `BookService` ontvangt `BookRepository` en `CategoryRepository`, `UserService` ontvangt de `PasswordEncoder` die in `SecurityConfig` als `@Bean` gedefinieerd is).

### Technische keuzes

- DTO's als `record`: entiteiten verlaten de API nooit. `UserResponse` bevat nooit het wachtwoord.
- ISBN als `String` als identificatie van het boek.
- `BigDecimal` voor de prijs, om afrondingsfouten van `double` te vermijden.
- Eigen excepties in plaats van generieke, om precieze HTTP-statuscodes te kunnen teruggeven (400, 404, 409).
- Wachtwoorden worden gehasht met BCrypt (eenrichtingshash met salt), nooit in klare tekst opgeslagen.
- JWT in plaats van sessies: de API is stateless. Het token is ondertekend met een geheime sleutel die alleen de server kent, zodat een client het niet kan vervalsen. Een token is 24 uur geldig.
- Een nieuwe gebruiker krijgt altijd de rol `USER`; de rol kan niet via de registratie gekozen worden.

## 3. Vereisten
 
- Java 17
- Maven (of de meegeleverde wrapper `mvnw`)
- MySQL
- PHP en Composer
- Node.js en npm (voor Vite)
  
## 4. Installatie en opstarten
 
### Backend
 
1. Maak een lege MySQL-database aan: `CREATE DATABASE pagestock;`
2. Kopieer in `backend/pagestock/src/main/resources/` het bestand `application.properties.example` naar `application.properties` en vul de MySQL-URL, gebruiker en wachtwoord in, evenals het JWT-geheim:
```properties
jwt.secret=een-lange-geheime-zin-van-minstens-32-tekens
jwt.expiration-ms=86400000
```
   Het geheim moet minstens 32 tekens lang zijn (256 bits), anders start de applicatie niet.
3. Starten:
```bash
cd backend/pagestock
./mvnw spring-boot:run
```
 
De API is bereikbaar op `http://localhost:8080`

### Eerste admin aanmaken

Elke nieuwe gebruiker krijgt de rol `USER`. Om een admin te maken: registreer een gebruiker via `POST /auth/register` en voer daarna in MySQL uit:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'jouw@email.com';
```

### Frontend
 
```bash
cd frontend/pagestock
cp .env.example .env
composer install
php artisan key:generate
npm install
npm run dev
php artisan serve
```
 
Controleer in `.env` dat `API_URL` naar de backend wijst (bv. `http://localhost:8080`).
De applicatie is bereikbaar op `http://localhost:8000`.


## Documentatie van de endpoints

### Authenticatie

1. Registreer met `POST /auth/register`.
2. Log in met `POST /auth/login`: het antwoord bevat een token.
3. Stuur dat token mee in de header van elke beveiligde request:
```
Authorization: Bearer <token>
```

Betekenis van de kolom **Toegang**: *publiek* = geen token nodig; *user* = ingelogd (elke rol); *admin* = ingelogd met de rol `ADMIN`. Zonder geldig token geeft een beveiligde route **401**, met een te lage rol **403**.

| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| POST | `/auth/register` | publiek | Maakt een account aan (rol `USER`) | 201, 400 bij ongeldige velden of verschillende wachtwoorden, 409 als het e-mailadres al bestaat |
| POST | `/auth/login` | publiek | Geeft een JWT terug | 200, 400, 401 bij fout e-mailadres of wachtwoord |

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
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Catalogus
 
Elke resource (`/books`, `/authors`, `/categories`) heeft dezelfde routes. Lezen (`GET`) is publiek, aanmaken, wijzigen en verwijderen is voorbehouden aan een admin.

**NOG AAN TE VULLEN**
**NOG AAN TE VULLEN**
**NOG AAN TE VULLEN**
 
| Methode | URL | Toegang | Beschrijving | Antwoorden |
|---|---|---|---|---|
| GET | `/books` | publiek | Geeft alle boeken terug | 200 |
| GET | `/books/{id}` | publiek | Detail van een boek (id = ISBN) | 200, 404 |
| GET | `/books/search?title=...` | publiek | Zoeken op titel (hoofdletterongevoelig) | 200 |
| POST | `/books` | admin | Maakt een boek aan | 201, 409 als het ISBN al bestaat, 404 als de categorie onbekend is |
| PUT | `/books/{id}` | admin | Wijzigt een boek | 200, 404 |
| DELETE | `/books/{id}` | admin | Verwijdert een boek | 204, 404 |
| DELETE | `/categories/{id}` | admin | Verwijdert een categorie | 204, 404 als onbekend, 409 als er nog boeken aan gekoppeld zijn |
 
Voorbeeld van een body voor `POST /books` **NOG AAN TE VULLEN**:
 
```json
{

}
```
 
**NOG AAN TE VULLEN**: maak een gelijkaardige tabel voor `/authors` en `/categories` (verwachte bodies, echte statuscodes) en controleer dat de bovenstaande codes overeenkomen met je excepties (`@ResponseStatus`).

## 6. Tests
```bash
cd backend/pagestock
./mvnw test
```
 
| Test | Wat wordt gecontroleerd | Waarom kritiek |
|---|---|---|
| **NOG AAN TE VULLEN** | | |
 
De opdracht vraagt minstens 3 tests, waaronder het bijwerken van de voorraad.
## 7. Documentatie van de code

## 8. Bronnen en AI-Tools
### Documentatie en bronnen
- Documentatie van Spring Boot en Spring Data JPA (https://docs.spring.io)
- Documentatie van Spring Security (https://docs.spring.io/spring-security/reference/)
- Documentatie van de bibliotheek jjwt (https://github.com/jwtk/jjwt)
- Documentatie van Laravel (https://laravel.com/docs)
### AI-tools
- **Claude (Anthropic)** gebruikt voor:
  - mijn code nalezen en advies over architectuur 
  - syntaxfouten te verbeteren
  - uitleg over afgeleide Spring Data-methodes (`existsByCategoryId`, `countByCategoryId`);
  - de authenticatie (Spring Security en JWT), die niet in de les behandeld werd. Hier was de hulp groter: Claude gaf de klassen `SecurityConfig`, `CustomUserDetailsService`, `JwtService` en `JwtAuthenticationFilter` en de methodes `register` (controller) en `login` (service) met uitleg in commentaar. Ik heb ze zelf overgetypt, met controlevragen om na te gaan of ik ze begreep. De DTO's, de repository, de excepties en `UserService.register` heb ik zelf geschreven en laten nalezen. Details staan in [docs/journal-auth.md](docs/journal-auth.md).
  - het opstellen van een eerste versie van deze README en van de delen over de authenticatie, die ik nagelezen en aangevuld heb.
- De rest van de code van het project heb ik zelf geschreven; de AI gaf me gerichte verbeteringen en uitleg, die ik begrepen heb voor ik ze toepaste.

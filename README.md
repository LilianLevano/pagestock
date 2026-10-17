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
- ...

## 2. Architecture

```
Browser --> Laravel-frontend (Blade) --> Spring Boot REST API --> MySQL
```
 
De repository bevat twee afzonderlijke applicaties:
 
- `backend/pagestock`: Spring Boot REST API (Java 17, Maven)
- `frontend/pagestock`: Laravel-applicatie die de API aanroept (URL ingesteld via `API_URL` in `.env`)


### Opbouw van de backend
 
Gelaagde architectuur, opgedeeld per domein (catalog, user, reservation, review):
 
- `controller`: stelt de REST-routes beschikbaar en zet resultaten om naar HTTP-statuscodes
- `service`: bedrijfslogica (bv. `BookService`)
- `repository`: toegang tot de data
- `model`: JPA-entiteiten
- `dto`: in- en uitvoerobjecten (`record`), zodat entiteiten nooit blootgesteld worden
- `exceptions`: eigen excepties (`ItemNotFoundException`, `BookAlreadyExistsException`), centraal afgehandeld

### Gebruikte patronen
 
- **MVC**: model = entiteiten en DTO's; controllers = `controller`-klassen in de API en Laravel-controllers in de frontend; view = Blade-templates (de JSON-respons van de API vervult de rol van view in de backend).
- **DAO**: de interfaces `BookRepository`, `CategoryRepository`, … breiden `JpaRepository` uit en isoleren de toegang tot de database (bv. `existsByCategoryId`, `findByTitleContainingIgnoreCase`).
- **IoC / dependency injection**: Spring maakt de beans aan en injecteert ze via de constructor (bv. `BookService` ontvangt `BookRepository` en `CategoryRepository`).

### Technische keuzes

- DTO's als `record`: entiteiten verlaten de API nooit.
- ISBN als `String` als identificatie van het boek.
- `BigDecimal` voor de prijs, om afrondingsfouten van `double` te vermijden.
- Eigen excepties in plaats van generieke, om precieze HTTP-statuscodes te kunnen teruggeven (404, 409).

## 3. Vereisten
 
- Java 17
- Maven (of de meegeleverde wrapper `mvnw`)
- MySQL
- PHP en Composer
- Node.js en npm (voor Vite)
  
## 4. Installatie en opstarten
 
### Backend
 
1. Maak een lege MySQL-database aan: `CREATE DATABASE pagestock;` **(NOG AAN TE VULLEN: echte naam van de database)**
2. Kopieer in `backend/pagestock/src/main/resources/` het bestand `application.properties.example` naar `application.properties` en vul de MySQL-URL, gebruiker en wachtwoord in, evenals het JWT-geheim. **(NOG AAN TE VULLEN: voeg dit `.example`-bestand toe aan de repository, zonder echt wachtwoord.)**
3. Starten:
```bash
cd backend/pagestock
./mvnw spring-boot:run
```
 
De API is bereikbaar op `http://localhost:8080` **(NOG AAN TE VULLEN: echte poort)**.

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
 
Elke resource (`/books`, `/authors`, `/categories`) heeft dezelfde routes.

**NOG AAN TE VULLEN**
**NOG AAN TE VULLEN**
**NOG AAN TE VULLEN**
 
| Methode | URL | Beschrijving | Antwoorden |
|---|---|---|---|
| GET | `/books` | Geeft alle boeken terug | 200 |
| GET | `/books/{id}` | Detail van een boek (id = ISBN) | 200, 404 |
| GET | `/books/search?title=...` | Zoeken op titel (hoofdletterongevoelig) | 200 |
| POST | `/books` | Maakt een boek aan | 201, 409 als het ISBN al bestaat, 404 als de categorie onbekend is |
| PUT | `/books/{id}` | Wijzigt een boek | 200, 404 |
| DELETE | `/books/{id}` | Verwijdert een boek | 204, 404 |
| DELETE | `/categories/{id}` | Verwijdert een categorie | 204, 404 als onbekend, 409 als er nog boeken aan gekoppeld zijn |
 
Voorbeeld van een body voor `POST /books` **NOG AAN TE VULLEN**:
 
```json
{

}
```
 
**NOG AAN TE VULLEN**: maak een gelijkaardige tabel voor `/authors` en `/categories` (verwachte bodies, echte statuscodes) en controleer dat de bovenstaande codes overeenkomen met je `@ControllerAdvice`.

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
- Documentatie van Laravel (https://laravel.com/docs)
### AI-tools
- **Claude (Anthropic)** gebruikt voor:
  - mijn code nalezen en advies over architectuur 
  - syntaxfouten te verbeteren
  - uitleg over afgeleide Spring Data-methodes (`existsByCategoryId`, `countByCategoryId`);
  - het opstellen van een eerste versie van deze README, die ik nagelezen en aangevuld heb.
- De code van het project heb ik zelf geschreven; de AI gaf me gerichte verbeteringen en uitleg, die ik begrepen heb voor ik ze toepaste.
 

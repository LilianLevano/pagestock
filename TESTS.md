# Functionele testen

## Admin bevestigt ophaling

```gherkin
Functionaliteit: Ophalingen bevestigen als een admin
Als ingelogde admin wil ik ophalingen kunnen bevestigen
wanneer de klant zijn boeken is komen ophalen,
zodat de voorraad correct wordt aangepast.

Achtergrond:
Gegeven ik ben ingelogd als "admin@gmail.com" met wachtwoord "admin1234"
En ik zit op de adminpagina met alle op te halen reservaties

Scenario: Ophaling bevestigen van een boek dat nog op voorraad is
Gegeven het boek "1985" heeft een voorraad van 5
En er is een reservatie met status "In afwachting" voor 2 exemplaren van "1985"
Als ik bij die reservatie op "Afgehaald" klik
Dan zie ik de melding "Afhaling bevestigd, de voorraad is aangepast."
En verdwijnt de reservatie uit de lijst van op te halen reservaties
En is de voorraad van "1985" 3

Scenario: Ophaling bevestigen terwijl de voorraad intussen te laag is geworden
Gegeven het boek "1985" heeft een voorraad van 1
omdat een andere reservatie van dat boek eerst werd opgehaald
En er is een reservatie met status "In afwachting" voor 2 exemplaren van "1985"
Als ik bij die reservatie op "Afgehaald" klik
Dan zie ik de melding "You can't order that many of that book (9780451524935). There are only: 1 left."
En blijft de reservatie in de lijst staan met status "In afwachting"
En blijft de voorraad van "1985" 1
```

## Boek toevoegen aan winkelmandje

```gherkin
Functionaliteit: Boek toevoegen aan winkelmandje
Als gebruiker wil ik vanaf de startpagina of de detailpagina een boek aan mijn winkelmandje toevoegen
zodat ik het daarna kan reserveren.

Scenario: Een boek toevoegen aan een leeg winkelmandje
Gegeven ik ben ingelogd als "user@gmail.com" met wachtwoord "user1234"
En mijn winkelmandje is leeg
En het boek "Clean Code" heeft een voorraad van 5
En ik zit op de homepage of detailpagina 
Als ik op "+" klik bij "Clean Code"
Dan zie ik de melding "Boek toegevoegd aan je winkelmandje."
En staat "Clean Code" in mijn winkelmandje met aantal 1
En is de voorraad van "Clean Code" nog steeds 5

Scenario: Hetzelfde boek een tweede keer toevoegen
Gegeven ik ben ingelogd als "user@gmail.com" met wachtwoord "user1234"
En "Clean Code" staat al in mijn winkelmandje met aantal 1
En ik zit op de homepagina of detailpagina van "Clean Code"
Als ik op "+" klik
Dan staat "Clean Code" één keer in mijn winkelmandje met aantal 2

Scenario: Een boek toevoegen zonder ingelogd te zijn
Gegeven ik ben niet ingelogd
En ik zit op de detailpagina van "Clean Code"
Als ik op "In winkelmandje" klik
Dan word ik doorgestuurd naar de loginpagina
En wordt het boek niet aan een winkelmandje toegevoegd
```


## Review schrijven

```gherkin
Functionaliteit: Review schrijven over een boek
  Als ingelogde gebruiker wil ik een review schrijven over een boek dat ik heb afgehaald
  zodat andere klanten mijn mening kunnen lezen.

  Achtergrond:
    Gegeven ik ben ingelogd als "user@gmail.com" met wachtwoord "user1234"

  Scenario: Review schrijven over een afgehaald boek
    Gegeven ik heb een reservatie voor "Clean Code" met status "Afgehaald"
    En ik heb nog geen review geschreven voor "Clean Code"
    En ik zit op de detailpagina van "Clean Code"
    Als ik op "Review toevoegen" klik
    En ik 4 sterren kies
    En ik "Heel leerrijk boek" invul bij "Je review"
    En ik op "Review plaatsen" klik
    Dan word ik teruggestuurd naar de detailpagina van "Clean Code"
    En zie ik de melding "Bedankt voor je review!"
    En staat mijn review met 4 sterren en "Heel leerrijk boek" in de lijst met reviews

  Scenario: Review schrijven over een boek dat ik niet heb afgehaald
    Gegeven ik heb geen reservatie voor "1985" met status "Afgehaald"
    En ik zit op het reviewformulier van "1985"
    Als ik 5 sterren kies
    En ik "Top boek" invul bij "Je review"
    En ik op "Review plaatsen" klik
    Dan zie ik de foutmelding "The book (isbnNumber) is not picked up. You cannot post a review about a book you don't have."
    En wordt er geen review toegevoegd aan "1985"

  Scenario: Een tweede review schrijven over hetzelfde boek
    Gegeven ik heb al een review geschreven voor "Clean Code"
    En ik zit op het reviewformulier van "Clean Code"
    Als ik 3 sterren kies
    En ik "Toch minder goed dan gedacht" invul bij "Je review"
    En ik op "Review plaatsen" klik
    Dan zie ik de foutmelding "You cannot post a second review on a book."
    En staat er nog steeds maar één review van mij bij "Clean Code"
```

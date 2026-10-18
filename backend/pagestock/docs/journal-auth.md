# AI-logboek: authenticatie (PageStock)

Dit logboek beschrijft hoe ik Claude (Anthropic) heb gebruikt bij de authenticatie van de backend (Spring Security + JWT), op 18/10/2026. Authenticatie werd niet in de les gezien, daarom heb ik hier meer hulp gevraagd dan voor de rest van het project. Werkwijze: voor de klassen die specifiek zijn voor Spring Security kreeg ik code met uitleg in commentaar, die ik zelf heb overgetypt (geen copy-paste) en waarover Claude me controlevragen stelde. De andere klassen heb ik zelf geschreven en liet ik nalezen.

## Code waarbij Claude rechtstreeks geholpen heeft

| Bestand | Hulp van Claude |
|---|---|
| `config/SecurityConfig` | Volledige klasse met commentaar: `PasswordEncoder` (BCrypt), `SecurityFilterChain` (publieke routes, CSRF uit, stateless), daarna de bean `AuthenticationManager`, de regel `/error`, de rol `ADMIN` op de catalogus, het 401-antwoord en het inpluggen van de JWT-filter. |
| `user/service/CustomUserDetailsService` | Volledige klasse met commentaar (gebruiker laden via e-mail). |
| `security/JwtService` | Volledige klasse met commentaar (token maken en controleren met jjwt). |
| `security/JwtAuthenticationFilter` | Volledige klasse met commentaar (header `Authorization: Bearer` lezen). |
| `user/controller/AuthController` | Methode `register` gegeven op mijn vraag. |
| `user/service/UserService` | Methode `login` gegeven (oproep naar de `AuthenticationManager` en aanmaak van het token). |
| `pom.xml`, `application.properties` | Welke dependencies (Spring Security, jjwt) en eigenschappen (`jwt.secret`, `jwt.expiration-ms`, `include-binding-errors`) toe te voegen. |

## Code die ik zelf geschreven heb, met nalezing door Claude

| Bestand | Hulp van Claude |
|---|---|
| `RegisterRequest`, `UserResponse`, `LoginRequest`, `LoginResponse` | Nagelezen: validatieannotaties toevoegen, naam van de velden (`firstName`), `@Size` niet bij login, JPA-annotatie `@Column` hoort niet in een DTO. |
| `UserRepository` | Nagelezen: interface moest `public` zijn. |
| `EmailAlreadyExistsException`, `PasswordNotEqualsConfirmPasswordException` | Advies over de HTTP-code (409 tegenover 400). |
| `UserService.register` | Zelf geschreven, nagelezen: bug gevonden (wachtwoord werd met zichzelf vergeleken in plaats van met `confirmPassword`), e-mail normaliseren, volgorde van de controles. |
| Endpoint `POST /auth/login` | Zelf geschreven, nagelezen. |

## Debugging

- **401 op `/auth/register`**: de namen van de JSON-sleutels kwamen niet overeen met het DTO, en de route `/error` was niet publiek. Daardoor werd een 400 (of 409) verborgen achter een 401.
- **Algemeen validatiebericht**: de veldberichten worden pas getoond met `include-binding-errors=always`.

## Uitleg en begrip

Claude heeft de concepten uitgelegd (filterketen, beans en IoC, BCrypt en salt, JWT en handtekening, stateless, 401 tegenover 403) en me controlevragen gesteld om na te gaan of ik alles begreep.

## Resultaat

Registratie, login met JWT, de filter en de rollen werken. Getest met Postman: `GET /books` zonder token geeft 200, `POST /authors` zonder token of met een vervalst token geeft 401, met een token van een `USER` geeft 403, en met een token van een `ADMIN` geeft 201.

<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

/**
 * Beheer van de boeken door een admin: lijst, toevoegen, wijzigen en verwijderen.
 *
 * Alle routes staan onder /admin, achter {@see \App\Http\Middleware\EnsureApiToken} en
 * {@see \App\Http\Middleware\EnsureAdmin}. Toevoegen en wijzigen gebruiken dezelfde view
 * admin.books.form.
 */
class AdminBookController extends Controller
{
    /**
     * Maakt een HTTP-client voor de API met de basis-URL en het JWT-token uit de sessie.
     *
     * @return \Illuminate\Http\Client\PendingRequest
     */
    private function api()
    {
        return Http::withToken(session('token'))->baseUrl(config('services.backend.url'));
    }

    /**
     * Zet een mislukte API-call om naar een redirect met een foutmelding.
     *
     * Bij 401 wordt het token uit de sessie gehaald en volgt een redirect naar /login. Bij 409
     * wordt de melding van de API getoond, anders de standaardmelding. De ingevulde velden
     * worden teruggezet in het formulier.
     *
     * @param  \Illuminate\Http\Client\Response  $response  het mislukte antwoord van de API
     * @param  string                            $default   melding die wordt getoond als de API geen bruikbare melding geeft
     * @return \Illuminate\Http\RedirectResponse
     */
    private function error($response, string $default)
    {
        // 401: token verlopen -> opnieuw inloggen
        if ($response->status() === 401) {
            session()->forget('token');
            return redirect()->route('login')->withErrors(['auth' => 'Je sessie is verlopen. Log opnieuw in.']);
        }

        // 409 komt van onze eigen exceptions (bv. ISBN bestaat al): die boodschap tonen we.
        // Andere fouten (bv. 400) geven een technische boodschap, dus dan onze eigen tekst.
        $message = $response->status() === 409 ? ($response->json('message') ?? $default) : $default;

        return back()->withErrors(['admin' => $message])->withInput();
    }

    /**
     * Geeft de validatieregels van het boekformulier terug.
     *
     * @param  bool  $withIsbn  true bij het aanmaken (ISBN-nummer verplicht), false bij het wijzigen
     * @return array<string, mixed> de validatieregels per veld
     */
    private function rules(bool $withIsbn): array
    {
        $rules = [
            'title' => 'required|string',
            'description' => 'required|string',
            'price' => 'required|numeric|min:0',
            'publicationDate' => 'required|date',
            'stock' => 'required|integer|min:0',
            'categoryId' => 'required|integer',
            'authorIds' => 'required|array|min:1',
        ];

        // Het ISBN kan enkel bij het aanmaken gekozen worden (BookUpdateRequest heeft geen ISBN)
        if ($withIsbn) {
            $rules['isbnNumber'] = ['required', 'regex:/^(\d{9}[0-9X]|\d{13})$/'];
        }

        return $rules;
    }

    /**
     * Haalt de auteurs en categorieën op voor de keuzelijsten in het formulier.
     *
     * @return array{authors: array, categories: array} lege lijsten als de API geen data teruggeeft
     */
    private function formData(): array
    {
        return [
            'authors' => $this->api()->get('/authors')->json() ?? [],
            'categories' => $this->api()->get('/categories')->json() ?? [],
        ];
    }

    /**
     * Toont alle boeken.
     *
     * Route: GET /admin/books
     *
     * @return \Illuminate\View\View de view admin.books.index met $books
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException met de statuscode van de API als die faalt
     */
    public function index()
    {
        $response = $this->api()->get('/books');

        if ($response->failed()) {
            abort($response->status());
        }

        $books = $response->json();

        return view('admin.books.index', compact('books'));
    }

    /**
     * Toont het lege formulier om een boek toe te voegen.
     *
     * Route: GET /admin/books/create
     *
     * @return \Illuminate\View\View de view admin.books.form met $book = null, $authors en $categories
     */
    public function create()
    {
        return view('admin.books.form', ['book' => null] + $this->formData());
    }

    /**
     * Maakt een nieuw boek aan via POST /books van de API.
     *
     * Route: POST /admin/books
     *
     * @param  Request  $request  formulier met isbnNumber, title, description, price, publicationDate,
     *                            stock, categoryId en authorIds
     * @return \Illuminate\Http\RedirectResponse naar de boekenlijst bij succes, anders zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als het formulier ongeldig is
     */
    public function store(Request $request)
    {
        $data = $request->validate($this->rules(true));
        // Formuliervelden zijn tekst: de id's omzetten naar getallen voor Set<Long> in Spring
        $data['authorIds'] = array_map('intval', $data['authorIds']);

        $response = $this->api()->post('/books', $data);

        if ($response->failed()) {
            return $this->error($response, 'Boek toevoegen mislukt, controleer de gegevens.');
        }

        return redirect()->route('admin.books.index')->with('success', 'Boek toegevoegd.');
    }

    /**
     * Toont het formulier om een bestaand boek te wijzigen, ingevuld met de huidige gegevens.
     *
     * Route: GET /admin/books/{isbn}/edit
     *
     * @param  string  $isbn  ISBN-nummer van het boek
     * @return \Illuminate\View\View de view admin.books.form met $book, $authors en $categories
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException 404 als het boek niet geladen kan worden
     */
    public function edit($isbn)
    {
        $response = $this->api()->get('/books/' . $isbn);

        if ($response->failed()) {
            abort(404);
        }

        return view('admin.books.form', ['book' => $response->json()] + $this->formData());
    }

    /**
     * Werkt een bestaand boek bij via PUT /books/{isbn} van de API. Het ISBN-nummer zelf kan
     * niet worden gewijzigd.
     *
     * Route: PUT /admin/books/{isbn}
     *
     * @param  Request  $request  formulier met title, description, price, publicationDate, stock,
     *                            categoryId en authorIds
     * @param  string   $isbn     ISBN-nummer van het boek
     * @return \Illuminate\Http\RedirectResponse naar de boekenlijst bij succes, anders zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als het formulier ongeldig is
     */
    public function update(Request $request, $isbn)
    {
        $data = $request->validate($this->rules(false));
        $data['authorIds'] = array_map('intval', $data['authorIds']);

        $response = $this->api()->put('/books/' . $isbn, $data);

        if ($response->failed()) {
            return $this->error($response, 'Boek wijzigen mislukt, controleer de gegevens.');
        }

        return redirect()->route('admin.books.index')->with('success', 'Boek gewijzigd.');
    }

    /**
     * Verwijdert een boek via DELETE /books/{isbn} van de API.
     *
     * Route: DELETE /admin/books/{isbn}
     *
     * @param  string  $isbn  ISBN-nummer van het boek
     * @return \Illuminate\Http\RedirectResponse terug naar de vorige pagina, met een succesmelding
     *                                           of zie {@see error()}
     */
    public function destroy($isbn)
    {
        $response = $this->api()->delete('/books/' . $isbn);

        if ($response->failed()) {
            return $this->error($response, 'Boek verwijderen mislukt.');
        }

        return back()->with('success', 'Boek verwijderd.');
    }
}

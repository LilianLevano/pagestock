<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

// Beheer van de boeken: enkel voor admins (middleware EnsureAdmin + hasRole("ADMIN") in Spring)
class AdminBookController extends Controller
{
    // Hulpfunctie: basis-URL van de API + Bearer-token uit de sessie
    private function api()
    {
        return Http::withToken(session('token'))->baseUrl(config('services.backend.url'));
    }

    // Hulpfunctie: een mislukte API-call omzetten naar een redirect met foutmelding
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

    // Validatieregels, dezelfde als BookRequest / BookUpdateRequest in Spring
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

    // Auteurs en categorieën ophalen voor de keuzelijsten in het formulier
    private function formData(): array
    {
        return [
            'authors' => $this->api()->get('/authors')->json() ?? [],
            'categories' => $this->api()->get('/categories')->json() ?? [],
        ];
    }

    // Lijst van alle boeken
    public function index()
    {
        $response = $this->api()->get('/books');

        if ($response->failed()) {
            abort($response->status());
        }

        $books = $response->json();

        return view('admin.books.index', compact('books'));
    }

    // Formulier om een nieuw boek toe te voegen ($book = null)
    public function create()
    {
        return view('admin.books.form', ['book' => null] + $this->formData());
    }

    // Nieuw boek opslaan
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

    // Formulier om een bestaand boek te wijzigen (zelfde view als create)
    public function edit($isbn)
    {
        $response = $this->api()->get('/books/' . $isbn);

        if ($response->failed()) {
            abort(404);
        }

        return view('admin.books.form', ['book' => $response->json()] + $this->formData());
    }

    // Wijzigingen opslaan
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

    // Boek verwijderen
    public function destroy($isbn)
    {
        $response = $this->api()->delete('/books/' . $isbn);

        if ($response->failed()) {
            return $this->error($response, 'Boek verwijderen mislukt.');
        }

        return back()->with('success', 'Boek verwijderd.');
    }
}

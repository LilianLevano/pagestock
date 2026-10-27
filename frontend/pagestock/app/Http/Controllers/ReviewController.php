<?php

namespace App\Http\Controllers;

use Illuminate\Http\Client\ConnectionException;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;

/**
 * Reviews schrijven voor een boek.
 *
 * Alle routes staan achter {@see \App\Http\Middleware\EnsureApiToken}. De API controleert of
 * de gebruiker het boek heeft afgehaald en nog geen review heeft geschreven.
 */
class ReviewController extends Controller
{
    /**
     * Toont het formulier om een review te schrijven, met de titel van het boek.
     *
     * Route: GET /books/{isbn}/reviews/create
     *
     * @param string $isbn ISBN-nummer van het boek
     * @return View|RedirectResponse de view reviews.create met $book,
     *                                                                 of terug als het boek niet bestaat
     * @throws ConnectionException
     */
    public function create($isbn)
    {
        // Het boek ophalen om de titel te kunnen tonen
        $response = Http::get(config('services.backend.url') . '/books/' . $isbn);


        if ($response->failed()) {
            return back()->withErrors(['error' => $response->json('message') ?? 'Dit boek bestaat niet.']);
        }

        $book = $response->json();

        return view('reviews.create', compact('book'));
    }

    /**
     * Stuurt de review naar POST /books/{isbn}/reviews van de API.
     *
     * Foutafhandeling: 403 (boek niet afgehaald) en 409 (al een review) -> terug naar het
     * formulier met de melding van de API; andere fouten -> algemene melding. Een 401 wordt
     * centraal afgehandeld via {@see Controller::api()}.
     *
     * Route: POST /books/{isbn}/reviews
     *
     * @param  Request  $request  formulier met rating (1 tot 5) en comment (max. 2000 tekens)
     * @param  string   $isbn     ISBN-nummer van het boek
     * @return RedirectResponse naar de detailpagina van het boek bij succes,
     *                                           anders terug naar het formulier
     *
     * @throws ValidationException|ConnectionException als rating of comment ongeldig is
     */
    public function store(Request $request, $isbn)
    {
        // Zelfde regels als CreateReviewRequest in Spring
        $data = $request->validate([
            'rating' => 'required|integer|min:1|max:5',
            'comment' => 'required|string|max:2000',
        ]);

        // Beschermde route: api() stuurt de JWT uit de sessie mee als Bearer-token
        $response = $this->api()->post('/books/' . $isbn . '/reviews', $data);

        // 403: de gebruiker heeft dit boek nog niet afgehaald
        if ($response->status() === 403) {
            return back()
                ->withErrors(['comment' => $response->json('message') ?? 'Je kan enkel een review schrijven over een boek dat je hebt afgehaald.'])
                ->withInput();
        }

        // 409: er bestaat al een review van deze gebruiker voor dit boek
        if ($response->status() === 409) {
            return back()
                ->withErrors(['comment' => $response->json('message') ?? 'Je hebt al een review geschreven voor dit boek.'])
                ->withInput();
        }

        // Andere fout (bv. 400)
        if ($response->failed()) {
            return back()
                ->withErrors(['comment' => 'Er ging iets mis, probeer opnieuw.'])
                ->withInput();
        }

        // Gelukt: terug naar de detailpagina van het boek
        return redirect()->route('catalog.show', $isbn)->with('success', 'Bedankt voor je review!');
    }
}

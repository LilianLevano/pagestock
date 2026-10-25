<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

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
     * @param  string  $isbn  ISBN-nummer van het boek
     * @return \Illuminate\View\View|\Illuminate\Http\RedirectResponse de view reviews.create met $book,
     *                                                                 of terug als het boek niet bestaat
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
     * Foutafhandeling: 401 -> redirect naar /login; 403 (boek niet afgehaald) en 409 (al een
     * review) -> terug naar het formulier met de melding van de API; andere fouten -> algemene melding.
     *
     * Route: POST /books/{isbn}/reviews
     *
     * @param  Request  $request  formulier met rating (1 tot 5) en comment (max. 2000 tekens)
     * @param  string   $isbn     ISBN-nummer van het boek
     * @return \Illuminate\Http\RedirectResponse naar de detailpagina van het boek bij succes,
     *                                           anders naar /login of terug naar het formulier
     *
     * @throws \Illuminate\Validation\ValidationException als rating of comment ongeldig is
     */
    public function store(Request $request, $isbn)
    {
        // Zelfde regels als CreateReviewRequest in Spring
        $data = $request->validate([
            'rating' => 'required|integer|min:1|max:5',
            'comment' => 'required|string|max:2000',
        ]);

        // Beschermde route: de JWT uit de sessie meesturen als Bearer-token
        $response = Http::withToken(session('token'))
            ->post(config('services.backend.url') . '/books/' . $isbn . '/reviews', $data);

        // 401: token verlopen of ongeldig -> opnieuw inloggen
        if ($response->status() === 401) {
            session()->forget('token');
            return redirect()->route('login')->withErrors(['error' => "U bent niet ingelogd."]);
        }

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

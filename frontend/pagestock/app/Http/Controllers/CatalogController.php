<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


/**
 * Publieke catalogus: lijst van boeken en detailpagina van één boek.
 *
 * Gebruikt alleen publieke GET-endpoints van de API, dus zonder token.
 */
class CatalogController extends Controller
{
    /**
     * Toont alle boeken, of alleen de boeken waarvan de titel de zoekterm bevat.
     *
     * Roept GET /books of GET /books/search?title=... van de API aan. Als de API faalt,
     * wordt een lege lijst met een foutmelding getoond (geen redirect, om een lus te vermijden).
     *
     * Route: GET /
     *
     * @param  Request  $request  request met de optionele queryparameter search
     * @return \Illuminate\View\View de view welcome met de variabele $books
     */
    public function index(Request $request){

        $search = $request->query('search');


        if($search){
            $response = Http::get(config("services.backend.url"). "/books/search", ['title' => $search]);
        }else{
            $response = Http::get(config("services.backend.url" ) . "/books");
        }

        $books = $response->json();

        // Fout van de API: lege lijst tonen met een melding (geen redirect, anders een lus)
        if ($response->failed()) {
            return view('welcome', ['books' => []])
                ->withErrors(['book' => 'De boeken konden niet geladen worden.']);
        }

        return view('welcome', compact('books'));
    }

    /**
     * Toont de detailpagina van een boek met zijn reviews.
     *
     * Roept GET /books/{isbn} en GET /books/{isbn}/reviews van de API aan. Als de reviews
     * niet geladen kunnen worden, wordt het boek toch getoond met een lege lijst.
     *
     * Route: GET /books/{id}
     *
     * @param  string  $isbnNumber  ISBN-nummer van het boek
     * @return \Illuminate\View\View de view books.book met $book en $reviews
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException met de statuscode van de API
     *                                                               (bv. 404) als het boek niet geladen kan worden
     */
    public function show($isbnNumber)
    {
        $response = Http::get(config('services.backend.url') . '/books/' . $isbnNumber);

        // Boek bestaat niet (of API-fout) -> 404-pagina
        if ($response->failed()) {
            abort($response->status(), $response->json('message') ?? 'Dit boek bestaat niet.');
        }

        $book = $response->json();

        // Reviews ophalen; als dat mislukt, tonen we het boek toch, met een lege lijst
        $reviewsResponse = Http::get(config('services.backend.url') . '/books/' . $isbnNumber . '/reviews');
        $reviews = $reviewsResponse->successful() ? $reviewsResponse->json() : [];

        return view('books.book', compact('book', 'reviews'));
    }
}

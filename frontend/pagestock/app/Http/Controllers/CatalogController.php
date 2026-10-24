<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


class CatalogController extends Controller
{
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

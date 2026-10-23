<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


class CatalogController extends Controller
{
    public function index(Request $request){


        $response = Http::get(config("services.backend.url" ) . "/books");
        $books = $response->json();
        $search = $request->query('search');
        if ($search) {
            $books = collect($books)
                ->filter(fn ($book) => str_contains(strtolower($book['title']), strtolower($search)))
                ->values()
                ->all();
        }

        return view('welcome', compact('books'));
    }

    public function show($isbnNumber){

        $response = Http::get(config("services.backend.url") . "/books/" . $isbnNumber);
        $book = $response->json();
        $reviews = Http::get(config("services.backend.url") . "/books/" . $isbnNumber . "/reviews");
        $reviews = $reviews->json();

        return view('book', compact('book', 'reviews'));
    }
}

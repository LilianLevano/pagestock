<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


class CatalogController extends Controller
{
    public function index(Request $request){

        $search = $request->query('search');

        $response = Http::get(config("services.backend.url" ) . "/books");
        $books = $response->json();

        if($search){
            $response = Http::get(config("services.backend.url"). "/books/search", ['title' => $search]);
            $books = $response->json();
            return view('welcome', compact('books'));
        }

        if ($response->failed()) {
            return back()->withErrors(['book' => $response->json('message')]);
        }

        return view('welcome', compact('books'));
    }

    public function show($isbnNumber){

        $response = Http::get(config("services.backend.url") . "/books/" . $isbnNumber);

        if($response->status() === 404){
            return back()->withErrors(['book' => $response->json('message')]);
        }

        if($response->failed()){
            return back()->withErrors(['book' => $response->json('message')]);
        }

        $book = $response->json();

        $reviews = Http::get(config("services.backend.url") . "/books/" . $isbnNumber . "/reviews");

        if ($reviews->failed()) {
            return back()->withErrors(['review' => $reviews->json('message')]);
        }

        $reviews = $reviews->json();

        return view('books.book', compact('book', 'reviews'));
    }
}

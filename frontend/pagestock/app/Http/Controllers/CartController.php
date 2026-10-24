<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

// Winkelmandje: bestaat enkel in de Laravel-sessie, de API kent het niet.
// Vorm van de sessie: 'cart' => [ isbn => aantal, ... ]
class CartController extends Controller
{
    // Toont het winkelmandje met titel en prijs van elk boek
    public function index()
    {
        $cart = session('cart', []);

        // Eén API-call voor alle boeken, daarna enkel de boeken uit het mandje houden
        $books = Http::get(config('services.backend.url') . '/books')->json();

        if ($books->failed()) {
            return redirect()->route('catalog.index')->withErrors(['error' => $books->json('message')]);
        }

        $items = collect($books)
            ->filter(fn ($book) => isset($cart[$book['isbnNumber']]))
            ->map(fn ($book) => [
                'isbn' => $book['isbnNumber'],
                'title' => $book['title'],
                'price' => $book['price'],
                'quantity' => $cart[$book['isbnNumber']],
            ])
            ->values();

        // Totaalprijs = som van prijs * aantal
        $total = $items->sum(fn ($item) => $item['price'] * $item['quantity']);

        return view('reservations.cart-index', compact('items', 'total'));
    }

    // Eén exemplaar van een boek toevoegen (knop "+" op de kaart)
    public function add($isbn)
    {
        $cart = session('cart', []);
        // Bestaat het boek al in het mandje? Dan +1, anders beginnen bij 1
        $cart[$isbn] = ($cart[$isbn] ?? 0) + 1;
        session(['cart' => $cart]);

        return back()->with('success', 'Boek toegevoegd aan je winkelmandje.');
    }

    // Een boek volledig uit het mandje verwijderen
    public function remove($isbn)
    {
        $cart = session('cart', []);
        unset($cart[$isbn]);
        session(['cart' => $cart]);

        return back();
    }
}

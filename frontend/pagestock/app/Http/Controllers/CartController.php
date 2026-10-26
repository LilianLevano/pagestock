<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

/**
 * Winkelmandje van de ingelogde gebruiker.
 *
 * Het mandje bestaat alleen in de Laravel-sessie (sleutel cart, met per ISBN-nummer het
 * aantal exemplaren); de API kent het niet. Pas bij het reserveren
 * ({@see ReservationController::store()}) wordt het naar de API gestuurd.
 */
class CartController extends Controller
{
    /**
     * Toont het winkelmandje met titel, prijs, aantal en de totaalprijs.
     *
     * Haalt alle boeken op via GET /books en houdt alleen de boeken uit het mandje over.
     * Boeken die niet meer bestaan (bv. verwijderd door een admin) worden ook uit het mandje
     * in de sessie gehaald, met een melding voor de gebruiker. Anders zouden ze onzichtbaar
     * blijven en zou elke reservatie mislukken met een 404.
     *
     * Route: GET /cart
     *
     * @return \Illuminate\View\View|\Illuminate\Http\RedirectResponse de view reservations.cart-index
     *                                                                 met $items en $total, of een redirect
     *                                                                 naar de catalogus als de API faalt
     */
    public function index()
    {
        $cart = session('cart', []);


        // Eén API-call voor alle boeken, daarna enkel de boeken uit het mandje houden
        $response = Http::get(config('services.backend.url') . '/books');

        if ($response->failed()) {
            return redirect()->route('catalog.index')->withErrors(['error' => $response->json('message')]);
        }

       $books = $response->json();

        $items = collect($books)
            ->filter(fn ($book) => isset($cart[$book['isbnNumber']]))
            ->map(fn ($book) => [
                'isbn' => $book['isbnNumber'],
                'title' => $book['title'],
                'price' => $book['price'],
                'quantity' => $cart[$book['isbnNumber']],
            ])
            ->values();


        // Boeken die niet meer bestaan (bv. verwijderd door een admin) uit het mandje halen,
        // anders blijven ze onzichtbaar in de sessie en faalt elke reservatie met een 404.
        $validCart = $items->pluck('quantity', 'isbn')->all();


        $total = $items->sum(fn ($item) => $item['price'] * $item['quantity']);

        if (count($validCart) < count($cart)) {
            session(['cart' => $validCart]);

            return view('reservations.cart-index', compact('items', 'total'))->withErrors(['cart' => 'Een of meer boeken bestaan niet meer en werden uit je winkelmandje verwijderd.']);
        }

        return view('reservations.cart-index', compact('items', 'total'));

    }

    /**
     * Voegt één exemplaar van een boek toe aan het mandje. Bestaan en voorraad worden hier niet
     * gecontroleerd; dat doet de API pas bij het reserveren.
     *
     * Route: POST /cart/add/{isbn}
     *
     * @param  string  $isbn  ISBN-nummer van het boek
     * @return \Illuminate\Http\RedirectResponse terug naar de vorige pagina met een succesmelding
     */
    public function add($isbn)
    {
        $cart = session('cart', []);
        // Bestaat het boek al in het mandje? Dan +1, anders beginnen bij 1
        $cart[$isbn] = ($cart[$isbn] ?? 0) + 1;
        session(['cart' => $cart]);

        return back()->with('success', 'Boek toegevoegd aan je winkelmandje.');
    }

    /**
     * Verwijdert een boek volledig uit het mandje, ongeacht het aantal exemplaren.
     *
     * Route: POST /cart/remove/{isbn}
     *
     * @param  string  $isbn  ISBN-nummer van het boek
     * @return \Illuminate\Http\RedirectResponse terug naar de vorige pagina
     */
    public function remove($isbn)
    {
        $cart = session('cart', []);
        unset($cart[$isbn]);
        session(['cart' => $cart]);

        return back();
    }
}

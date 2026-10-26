<?php

namespace App\Http\Controllers;

/**
 * Reservaties van de ingelogde gebruiker: mandje omzetten naar een reservatie, eigen
 * reservaties bekijken en annuleren, en de afgehaalde boeken tonen.
 *
 * Alle routes staan achter {@see \App\Http\Middleware\EnsureApiToken}. Een 401 van de API
 * (token verlopen) wordt centraal afgehandeld via {@see Controller::api()}.
 */
class ReservationController extends Controller
{
    /**
     * Toont alle reservaties van de ingelogde gebruiker, de nieuwste eerst.
     *
     * Roept GET /reservations/me van de API aan.
     *
     * Route: GET /reservations
     *
     * @return \Illuminate\View\View|\Illuminate\Http\RedirectResponse de view reservations.reservations-index
     *                                                                 met $reservations, of een redirect bij een fout
     */
    public function index()
    {
        $response = $this->api()->get('/reservations/me');

        // Fout van de API -> terug naar de catalogus met een melding
        if ($response->failed()) {
            return redirect()->route('catalog.index')
                ->withErrors(['reservation' => 'Je reservaties konden niet geladen worden.']);
        }

        // Sorteren op id: nieuwste reservatie bovenaan
        $reservations = collect($response->json())->sortByDesc('id')->values();


        return view('reservations.reservations-index', compact('reservations'));
    }

    /**
     * Zet het winkelmandje om naar een reservatie via POST /reservations van de API.
     *
     * Bij succes wordt het mandje geleegd. Bij een fout blijft het mandje staan en wordt de
     * melding van de API getoond: 409 bij onvoldoende voorraad, 404 als een boek niet meer bestaat.
     *
     * Route: POST /reservations
     *
     * @return \Illuminate\Http\RedirectResponse naar "mijn reservaties" bij succes, anders terug naar
     *                                           het mandje met een foutmelding
     */
    public function store()
    {
        $cart = session('cart', []);

        if (empty($cart)) {
            return back()->withErrors(['cart' => 'Je winkelmandje is leeg.']);
        }

        // Mandje [isbn => aantal] omzetten naar het formaat van CreateReservationRequest
        $lines = collect($cart)
            ->map(fn ($quantity, $isbn) => ['bookIsbn' => (string) $isbn, 'quantity' => $quantity])
            ->values()
            ->all();

        $response = $this->api()->post('/reservations', ['lines' => $lines]);

        // 409: niet genoeg voorraad voor minstens één boek
        if ($response->status() === 409) {
            return back()->withErrors(['cart' => $response->json('message') ?? 'Niet genoeg voorraad.']);
        }

        // 404: een boek bestaat niet (meer)
        if ($response->status() === 404) {
            return back()->withErrors(['cart' => $response->json('message') ?? 'Boek niet gevonden.']);
        }

        if ($response->failed()) {
            return back()->withErrors(['cart' => $response->json('message') ?? 'Reservatie mislukt, probeer opnieuw.']);
        }

        // Gelukt: mandje leegmaken en naar "mijn reservaties"
        session()->forget('cart');

        return redirect()->route('reservations.index')->with('success', 'Je reservatie is geplaatst.');
    }

    /**
     * Annuleert een reservatie via PATCH /reservations/{id}/cancel van de API.
     *
     * Route: PATCH /reservations/{id}/cancel
     *
     * @param  int|string  $id  id van de reservatie
     * @return \Illuminate\Http\RedirectResponse terug naar de vorige pagina met een succes- of foutmelding
     */
    public function cancel($id)
    {
        $response = $this->api()->patch('/reservations/' . $id . '/cancel');

        // Bv. 403: reservatie is al afgehaald of geannuleerd -> de melding van Spring tonen
        if ($response->failed()) {
            return back()->withErrors(['reservation' => $response->json('message') ?? 'Annuleren mislukt.']);
        }

        return back()->with('success', 'Reservatie geannuleerd.');
    }

    /**
     * Toont de boeken die de gebruiker al heeft afgehaald, elk boek maar één keer, ook als het
     * in meerdere reservaties voorkwam.
     *
     * Roept GET /reservations/me/purchased van de API aan.
     *
     * Route: GET /my-books
     *
     * @return \Illuminate\View\View|\Illuminate\Http\RedirectResponse de view reservations.reservations-purchased
     *                                                                 met $books (reservatielijnen), of een redirect
     *                                                                 naar de catalogus als de API faalt
     */
    public function purchased()
    {
        $reservations = $this->api()->get('/reservations/me/purchased');
        if ($reservations->failed()) {
            return redirect()->route('catalog.index')
                ->withErrors(['reservation' => 'Je afgehaalde boeken konden niet geladen worden.']);
        }


    // Alle lijnen van alle afgehaalde reservaties samenvoegen, elk boek maar één keer
            $books = collect($reservations->json())
                ->flatMap(fn ($reservation) => $reservation['lines'])
                ->unique('bookIsbn')
                ->values();



        return view('reservations.reservations-purchased', compact('books'));
    }
}

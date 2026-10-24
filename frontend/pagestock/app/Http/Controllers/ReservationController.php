<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

class ReservationController extends Controller
{
    // Hulpfunctie: basis-URL van de API + Bearer-token uit de sessie
    private function api()
    {
        return Http::withToken(session('token'))->baseUrl(config('services.backend.url'));
    }

    // Lijst van de reservaties van de ingelogde gebruiker
    public function index()
    {
        $response = $this->api()->get('/reservations/me');

        // Token verlopen -> opnieuw inloggen
        if ($response->status() === 401) {
            session()->forget('token');
            return redirect()->route('login');
        }

        $reservations = $response->json() ?? [];

        return view('reservations.reservations-index', compact('reservations'));
    }

    // Het winkelmandje omzetten naar een reservatie
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


        if ($response->status() === 401) {
            session()->forget('token');
            return redirect()->route('login')->withErrors(['cart' =>  'U bent niet ingelogd. Log in of register.']);


        }

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

    // Een reservatie annuleren (alleen mogelijk zolang ze RESERVED is, Spring controleert dat)
    public function cancel($id)
    {
        $response = $this->api()->patch('/reservations/' . $id . '/cancel');


        if ($response->failed()) {
            return back()->withErrors(['reservation' => $response->json('message') ?? 'Annuleren mislukt.']);
        }

        return back()->with('success', 'Reservatie geannuleerd.');
    }

    // Boeken die de gebruiker al heeft afgehaald
    public function purchased()
    {
        $reservations = $this->api()->get('/reservations/me/purchased');
        if($reservations->failed()){
            return redirect()->route("catalog.index")->withErrors(['reservation' => $reservations->json('message') ?? 'Reservatie mislukt.']);
        }


    // Alle lijnen van alle afgehaalde reservaties samenvoegen, elk boek maar één keer
            $books = collect($reservations->json())
                ->flatMap(fn ($reservation) => $reservation['lines'])
                ->unique('bookIsbn')
                ->values();



        return view('reservations.reservations-purchased', compact('books'));
    }
}

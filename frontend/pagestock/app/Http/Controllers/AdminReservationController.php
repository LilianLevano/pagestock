<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

// Beheer van alle reservaties: enkel voor admins (middleware EnsureAdmin + controle in Spring)
class AdminReservationController extends Controller
{
    // Alle reservaties van alle gebruikers
    public function index()
    {
        $response = Http::withToken(session('token'))
            ->get(config('services.backend.url') . '/reservations');

        // 403: Spring weigert, de gebruiker is geen admin
        if ($response->failed()) {
            return redirect()->route('catalog.index')->withErrors(['error' => $response->json('message')]);
        }


        $reservations = $response->json() ?? [];

        return view('admin.reservations', compact('reservations'));
    }

    // Afhaling bevestigen: hier daalt de voorraad (in Spring, @Transactional)
    public function pickup($id)
    {
        $response = Http::withToken(session('token'))
            ->patch(config('services.backend.url') . '/reservations/' . $id . '/pickup');


        if ($response->failed()) {
            return back()->withErrors(['reservation' =>  $response->json('message') ?? 'Bevestigen mislukt.']);
        }

        return back()->with('success', 'Afhaling bevestigd, de voorraad is aangepast.');
    }
}

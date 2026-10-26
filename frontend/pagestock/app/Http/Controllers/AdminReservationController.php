<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

/**
 * Beheer van de reservaties door een admin: openstaande reservaties bekijken en afhalingen
 * bevestigen.
 *
 * Alle routes staan onder /admin, achter {@see \App\Http\Middleware\EnsureApiToken} en
 * {@see \App\Http\Middleware\EnsureAdmin}.
 */
class AdminReservationController extends Controller
{
    /**
     * Toont de reservaties die nog moeten worden afgehaald (status RESERVED), van alle gebruikers.
     *
     * Roept GET /reservations van de API aan, die alleen de openstaande reservaties teruggeeft.
     *
     * Route: GET /admin/reservations
     *
     * @return \Illuminate\View\View|\Illuminate\Http\RedirectResponse de view admin.reservations
     *                                                                 met $reservations, of een redirect
     *                                                                 naar de catalogus als de API faalt
     */
    public function index()
    {

        $response = $this->api()->get('/reservations');

        // Fout van de API (bv. 403); een 401 wordt al door api() afgehandeld
        if ($response->failed()) {
            return redirect()->route('catalog.index')->withErrors(['error' => $response->json('message') ?? 'Kan de reservaties niet laden.']);
        }


        $reservations = $response->json() ?? [];

        return view('admin.reservations', compact('reservations'));
    }

    /**
     * Bevestigt dat een reservatie is afgehaald via PATCH /reservations/{id}/pickup van de API.
     * Pas op dat moment daalt de voorraad van de boeken.
     *
     * Route: PATCH /admin/reservations/{id}/pickup
     *
     * @param  int|string  $id  id van de reservatie
     * @return \Illuminate\Http\RedirectResponse terug naar de vorige pagina met een succes- of foutmelding
     *                                           (bv. 409 als de voorraad intussen onvoldoende is)
     */
    public function pickup($id)
    {

        $response = $this->api()->patch('/reservations/' . $id . '/pickup' );

        if ($response->failed()) {
            return $this->error($response, "Kan de reservaties ophalen.");
        }

        return back()->with('success', 'Afhaling bevestigd, de voorraad is aangepast.');
    }
}

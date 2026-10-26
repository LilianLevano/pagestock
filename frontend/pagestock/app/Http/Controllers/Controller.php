<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Http;

/**
 * Basisklasse van alle controllers van de frontend.
 */
abstract class Controller
{
    /**
     * Maakt een HTTP-client voor de API met de basis-URL en het JWT-token uit de sessie.
     *
     * @return \Illuminate\Http\Client\PendingRequest
     */
    public function api()
    {
        return Http::withToken(session('token'))->baseUrl(config('services.backend.url'));
    }

    /**
     * Zet een mislukte API-call om naar een redirect met een foutmelding.
     *
     * Bij 401 wordt het token uit de sessie gehaald en volgt een redirect naar /login. Bij 409
     * (naam bestaat al, of de categorie bevat nog boeken) wordt de melding van de API getoond,
     * anders de standaardmelding.
     *
     * @param  \Illuminate\Http\Client\Response  $response  het mislukte antwoord van de API
     * @param  string                            $default   melding die wordt getoond als de API geen bruikbare melding geeft
     * @return \Illuminate\Http\RedirectResponse
     */
    public function error($response, string $default)
    {
        if ($response->status() === 401) {
            session()->forget(['token', 'role']);
            return redirect()->route('login')->withErrors(['auth' => 'Je sessie is verlopen. Log opnieuw in.']);
        }

        // 409: bv. nog gekoppeld aan boeken -> de boodschap van Spring tonen
        $message = $response->status() === 409 ? ($response->json('message') ?? $default) : $default;

        return back()->withErrors(['admin' => $message]);
    }
}

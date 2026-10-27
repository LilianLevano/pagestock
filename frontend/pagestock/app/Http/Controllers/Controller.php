<?php

namespace App\Http\Controllers;

use Illuminate\Http\Client\PendingRequest;
use Illuminate\Http\Client\Response;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\Http;

/**
 * Basisklasse van alle controllers van de frontend.
 */
abstract class Controller
{
    /**
     * Maakt een HTTP-client voor de API met de basis-URL en het JWT-token uit de sessie.
     *
     * Antwoordt de API met 401 (token verlopen of ongeldig), dan gooit de client een
     * {@see \Illuminate\Http\Client\RequestException}. Die wordt centraal opgevangen in
     * bootstrap/app.php: de gebruiker wordt uitgelogd en naar /login gestuurd. De controllers
     * hoeven een 401 dus nooit zelf af te handelen.
     *
     * @return PendingRequest
     */
    public function api()
    {
        return Http::withToken(session('token'))
            ->baseUrl(config('services.backend.url'))
            ->throwIf(fn ($response) => $response->status() === 401);
    }

    /**
     * Zet een mislukte API-call om naar een redirect met een foutmelding.
     *
     * Bij 409 (naam bestaat al, of nog gekoppeld aan boeken) wordt de melding van de API
     * getoond, anders de standaardmelding. Een 401 komt hier nooit aan: die wordt al door
     * {@see api()} afgehandeld.
     *
     * @param  Response  $response  het mislukte antwoord van de API
     * @param  string                            $default   melding die wordt getoond als de API geen bruikbare melding geeft
     * @return RedirectResponse
     */
    public function error($response, string $default)
    {
        // 409: bv. nog gekoppeld aan boeken -> de boodschap van Spring tonen
        $message = $response->status() === 409 ? ($response->json('message') ?? $default) : $default;

        return back()->withErrors(['admin' => $message])->withInput();
    }
}

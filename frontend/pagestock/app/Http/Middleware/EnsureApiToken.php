<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * Middleware die alleen ingelogde gebruikers doorlaat.
 *
 * "Ingelogd" betekent hier: er staat een JWT-token van de API in de sessie. Of dat token nog
 * geldig is, wordt niet gecontroleerd; dat merkt de controller pas als de API 401 antwoordt.
 */
class EnsureApiToken
{
    /**
     * Laat de request door als er een token in de sessie staat, anders volgt een redirect
     * naar de loginpagina met een foutmelding.
     *
     * @param  Request  $request  de binnenkomende request
     * @param  Closure  $next     de volgende stap in de middleware-keten
     * @return Response de response van de volgende stap, of een redirect naar /login
     */
    public function handle(Request $request, Closure $next): Response
    {
        // Geen token -> doorsturen naar de loginpagina
        if (!session()->has('token')) {
            return redirect()->route('login')->with('error', 'U bent niet ingelogd. Log in of register.');
        }

        // Wel een token -> de request mag verder naar de controller
        return $next($request);
    }
}

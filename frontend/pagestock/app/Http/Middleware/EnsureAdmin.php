<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * Middleware die alleen gebruikers met de rol ADMIN doorlaat.
 *
 * De rol komt uit de sessie (gezet bij het inloggen). Deze controle bepaalt alleen wat de
 * gebruiker te zien krijgt: de API controleert zelf opnieuw met hasRole("ADMIN").
 * Wordt altijd samen met {@see EnsureApiToken} gebruikt, binnen de groep /admin.
 */
class EnsureAdmin
{
    /**
     * Laat de request door als de gebruiker admin is, anders volgt een 403-pagina.
     *
     * @param  Request  $request  de binnenkomende request
     * @param  Closure  $next     de volgende stap in de middleware-keten
     * @return Response de response van de volgende stap
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException 403 als de rol in de sessie geen ADMIN is
     */
    public function handle(Request $request, Closure $next): Response
    {
        if (session('role') !== 'ADMIN') {
            abort(403);
        }

        return $next($request);
    }
}

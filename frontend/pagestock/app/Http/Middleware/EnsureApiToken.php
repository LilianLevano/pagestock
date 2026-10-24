<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

// Middleware: laat alleen ingelogde gebruikers door (= er staat een token in de sessie)
class EnsureApiToken
{
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

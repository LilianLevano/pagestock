<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

// Laat enkel admins door. Dit is alleen voor de weergave:
// de echte beveiliging blijft hasRole("ADMIN") in Spring.
class EnsureAdmin
{
    public function handle(Request $request, Closure $next): Response
    {
        if (session('role') !== 'ADMIN') {
            abort(403);
        }

        return $next($request);
    }
}

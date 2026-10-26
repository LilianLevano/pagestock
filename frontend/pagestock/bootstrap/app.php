<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Client\RequestException;
use Illuminate\Http\Request;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        //
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        $exceptions->shouldRenderJsonWhen(
            fn (Request $request) => $request->is('api/*') || $request->expectsJson(),
        );

        // 401 van de Spring-API (token verlopen of ongeldig), gegooid door Controller::api():
        // overal op dezelfde manier uitloggen en naar de loginpagina sturen.
        $exceptions->render(function (RequestException $e) {
            if ($e->response->status() === 401) {
                session()->forget(['token', 'role']);

                return redirect()->route('login')->with('error', 'Je sessie is verlopen. Log opnieuw in.');
            }
        });
    })->create();

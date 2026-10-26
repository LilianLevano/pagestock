<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

/**
 * Registratie, login en logout via de API (/auth).
 *
 * Na een geslaagde login worden het JWT-token en de rol in de Laravel-sessie bewaard. De andere
 * controllers sturen dat token mee als Bearer-token naar de API.
 */
class AuthController extends Controller
{
    /**
     * Toont het loginformulier.
     *
     * Route: GET /login
     *
     * @return \Illuminate\View\View de view auth.login
     */
    public function showLogin()
    {
        return view('auth.login');
    }

    /**
     * Verwerkt het loginformulier via POST /auth/login van de API.
     *
     * Bij succes worden token en rol in de sessie gezet en wordt de sessie-id vernieuwd
     * (tegen session fixation). Bij elke fout van de API (verkeerd wachtwoord, onbekend
     * e-mailadres, ...) toont het formulier dezelfde algemene foutmelding.
     *
     * Route: POST /login
     *
     * @param  Request  $request  formulier met email en password
     * @return \Illuminate\Http\RedirectResponse naar de catalogus bij succes, anders terug naar het formulier
     *
     * @throws \Illuminate\Validation\ValidationException als email of password ontbreekt of ongeldig is
     */
    public function login(Request $request)
    {
        $credentials = $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $response = Http::post(config('services.backend.url') . '/auth/login', $credentials);

        if ($response->failed()) {
            return back()
                ->withErrors(['email' => 'E-mailadres of wachtwoord is fout.'])
                ->onlyInput('email');
        }


        session(['token' => $response->json('token'), 'role' => $response->json('role')]);

        $request->session()->regenerate();

        return redirect()->route('catalog.index');
    }


    /**
     * Toont het registratieformulier.
     *
     * Route: GET /register
     *
     * @return \Illuminate\View\View de view auth.register
     */
    public function showRegister()
    {
        return view('auth.register');
    }


    /**
     * Verwerkt het registratieformulier via POST /auth/register van de API.
     *
     * De validatie volgt dezelfde regels als RegisterRequest in Spring. Bij een fout worden de
     * ingevulde velden teruggezet, behalve de wachtwoorden.
     *
     * Bij 409 toont het formulier de melding van de API, die zegt of het e-mailadres of het
     * telefoonnummer al in gebruik is. De melding verschijnt altijd bij het veld email.
     *
     * Route: POST /register
     *
     * @param  Request  $request  formulier met firstName, lastName, email, password, confirmPassword en phoneNumber
     * @return \Illuminate\Http\RedirectResponse naar /login bij succes; terug naar het formulier bij
     *                                           409 (e-mailadres of telefoonnummer al in gebruik) of een
     *                                           andere fout
     *
     * @throws \Illuminate\Validation\ValidationException als de formuliervelden ongeldig zijn
     */
    public function register(Request $request)
    {

        $data = $request->validate([
            'firstName' => 'required|max:50',
            'lastName' => 'required|max:50',
            'email' => 'required|email',
            'password' => 'required|min:8',
            'confirmPassword' => 'required|same:password',
            'phoneNumber' => 'required',
        ]);

        $response = Http::post(config('services.backend.url') . '/auth/register', $data);


        if ($response->status() === 409) {
            return back()
                ->withErrors(['error' => $response->json('message') ?? "Dit email of telefoonnummer is al in gebruik."])
                ->withInput($request->except('password', 'confirmPassword'));
        }


        if ($response->failed()) {
            return back()
                ->withErrors(['error' => $response->json('message') ?? 'Registratie mislukt, controleer je gegevens.'])
                ->withInput($request->except('password', 'confirmPassword'));
        }


        return redirect()->route('login')->with('success', 'Account aangemaakt, je kan nu inloggen.');
    }


    /**
     * Logt de gebruiker uit door de volledige sessie te wissen (token, rol en winkelmandje)
     * en een nieuw CSRF-token aan te maken. De API wordt niet aangeroepen: een JWT kan daar
     * niet worden ingetrokken en verloopt vanzelf.
     *
     * Route: POST /logout
     *
     * @param  Request  $request  de request van de ingelogde gebruiker
     * @return \Illuminate\Http\RedirectResponse naar de catalogus
     */
    public function logout(Request $request)
    {
        $request->session()->forget('token');
        $request->session()->invalidate();

        $request->session()->regenerateToken();

        return redirect()->route('catalog.index');
    }
}

<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

class AuthController extends Controller
{
    public function showLogin()
    {
        return view('auth.login');
    }

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


        session(['token' => $response->json('token')]);

        $request->session()->regenerate();

        return redirect()->route('catalog.index');
    }


    public function showRegister()
    {
        return view('auth.register');
    }


    public function register(Request $request)
    {

        $data = $request->validate([
            'firstName' => 'required',
            'lastName' => 'required',
            'email' => 'required|email',
            'password' => 'required|min:8',
            'confirmPassword' => 'required|same:password',
            'phoneNumber' => 'required',
        ]);

        $response = Http::post(config('services.backend.url') . '/auth/register', $data);


        if ($response->status() === 409) {
            return back()
                ->withErrors(['email' => 'Dit e-mailadres is al in gebruik.'])
                ->withInput($request->except('password', 'confirmPassword'));
        }


        if ($response->failed()) {
            return back()
                ->withErrors(['email' => 'Registratie mislukt, controleer je gegevens.'])
                ->withInput($request->except('password', 'confirmPassword'));
        }


        return redirect()->route('login')->with('success', 'Account aangemaakt, je kan nu inloggen.');
    }


    public function logout(Request $request)
    {
        $request->session()->forget('token');
        $request->session()->invalidate();

        $request->session()->regenerateToken();

        return redirect()->route('catalog.index');
    }
}

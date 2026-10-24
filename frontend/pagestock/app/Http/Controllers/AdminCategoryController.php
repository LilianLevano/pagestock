<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

// Beheer van de auteurs: alles op één pagina (lijst + toevoegen + wijzigen + verwijderen)
class AdminCategoryController extends Controller
{
    // Hulpfunctie: basis-URL van de API + Bearer-token uit de sessie
    private function api()
    {
        return Http::withToken(session('token'))->baseUrl(config('services.backend.url'));
    }

    // Hulpfunctie: een mislukte API-call omzetten naar een redirect met foutmelding
    private function error($response, string $default)
    {
        if ($response->status() === 401) {
            session()->forget('token');
            return redirect()->route('login')->withErrors(['auth' => 'Je sessie is verlopen. Log opnieuw in.']);
        }

        // 409: bv. nog gekoppeld aan boeken -> de boodschap van Spring tonen
        $message = $response->status() === 409 ? ($response->json('message') ?? $default) : $default;

        return back()->withErrors(['admin' => $message]);
    }

    // Lijst van alle auteurs
    public function index()
    {
        $response = $this->api()->get('/categories');

        if ($response->failed()) {
            abort($response->status());
        }

        $items = $response->json();

        return view('admin.categories.categories', compact('items'));
    }

    // Nieuwe auteur toevoegen
    public function store(Request $request)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->post('/categories', $data);

        if ($response->failed()) {
            return $this->error($response, 'Auteur toevoegen mislukt.');
        }

        return back()->with('success', 'Categorie toegevoegd.');
    }

    // Naam wijzigen
    public function update(Request $request, $id)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->put('/categories/' . $id, $data);

        if ($response->failed()) {
            return $this->error($response, 'Categorie wijzigen mislukt.');
        }

        return back()->with('success', 'Categorie gewijzigd.');
    }

    // Verwijderen (Spring geeft 409 als er nog boeken aan gekoppeld zijn)
    public function destroy($id)
    {
        $response = $this->api()->delete('/categories/' . $id);

        if ($response->failed()) {
            return $this->error($response, 'Categorie verwijderen mislukt.');
        }

        return back()->with('success', 'Categorie verwijderd.');
    }
}

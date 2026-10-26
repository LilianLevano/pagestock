<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

/**
 * Beheer van de categorieën door een admin, alles op één pagina: lijst, toevoegen, wijzigen
 * en verwijderen.
 *
 * Alle routes staan onder /admin, achter {@see \App\Http\Middleware\EnsureApiToken} en
 * {@see \App\Http\Middleware\EnsureAdmin}.
 */
class AdminCategoryController extends Controller
{
    /**
     * Maakt een HTTP-client voor de API met de basis-URL en het JWT-token uit de sessie.
     *
     * @return \Illuminate\Http\Client\PendingRequest
     */
    private function api()
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

    /**
     * Toont alle categorieën, met de formulieren om toe te voegen, te wijzigen en te verwijderen.
     *
     * Route: GET /admin/categories
     *
     * @return \Illuminate\View\View de view admin.categories.categories met $items
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException met de statuscode van de API als die faalt
     */
    public function index()
    {
        $response = $this->api()->get('/categories');

        if ($response->failed()) {
            abort($response->status());
        }

        $items = $response->json();

        return view('admin.categories.categories', compact('items'));
    }

    /**
     * Maakt een nieuwe categorie aan via POST /categories van de API.
     *
     * Route: POST /admin/categories
     *
     * @param  Request  $request  formulier met name
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als de naam ontbreekt
     */
    public function store(Request $request)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->post('/categories', $data);

        if ($response->failed()) {
            return $this->error($response, 'Categorie toevoegen mislukt.');
        }

        return back()->with('success', 'Categorie toegevoegd.');
    }

    /**
     * Wijzigt de naam van een categorie via PUT /categories/{id} van de API.
     *
     * Route: PUT /admin/categories/{id}
     *
     * @param  Request     $request  formulier met name
     * @param  int|string  $id       id van de categorie
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als de naam ontbreekt
     */
    public function update(Request $request, $id)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->put('/categories/' . $id, $data);

        if ($response->failed()) {
            return $this->error($response, 'Categorie wijzigen mislukt.');
        }

        return back()->with('success', 'Categorie gewijzigd.');
    }

    /**
     * Verwijdert een categorie via DELETE /categories/{id} van de API.
     *
     * Route: DELETE /admin/categories/{id}
     *
     * @param  int|string  $id  id van de categorie
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     */
    public function destroy($id)
    {
        $response = $this->api()->delete('/categories/' . $id);

        if ($response->failed()) {
            return $this->error($response, 'Categorie verwijderen mislukt.');
        }

        return back()->with('success', 'Categorie verwijderd.');
    }
}

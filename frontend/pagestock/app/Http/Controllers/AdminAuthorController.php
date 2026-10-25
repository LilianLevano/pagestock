<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

/**
 * Beheer van de auteurs door een admin, alles op één pagina: lijst, toevoegen, wijzigen en
 * verwijderen.
 *
 * Alle routes staan onder /admin, achter {@see \App\Http\Middleware\EnsureApiToken} en
 * {@see \App\Http\Middleware\EnsureAdmin}.
 */
class AdminAuthorController extends Controller
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
     * (naam bestaat al, of nog gekoppeld aan boeken) wordt de melding van de API getoond,
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
     * Toont alle auteurs, met de formulieren om toe te voegen, te wijzigen en te verwijderen.
     *
     * Route: GET /admin/authors
     *
     * @return \Illuminate\View\View de view admin.authors.authors met $items
     *
     * @throws \Symfony\Component\HttpKernel\Exception\HttpException met de statuscode van de API als die faalt
     */
    public function index()
    {
        $response = $this->api()->get('/authors');

        if ($response->failed()) {
            abort($response->status());
        }

        $items = $response->json();

        return view('admin.authors.authors', compact('items'));
    }

    /**
     * Maakt een nieuwe auteur aan via POST /authors van de API.
     *
     * Route: POST /admin/authors
     *
     * @param  Request  $request  formulier met name
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als de naam ontbreekt
     */
    public function store(Request $request)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->post('/authors', $data);

        if ($response->failed()) {
            return $this->error($response, 'Auteur toevoegen mislukt.');
        }

        return back()->with('success', 'Auteur toegevoegd.');
    }

    /**
     * Wijzigt de naam van een auteur via PUT /authors/{id} van de API.
     *
     * Route: PUT /admin/authors/{id}
     *
     * @param  Request     $request  formulier met name
     * @param  int|string  $id       id van de auteur
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws \Illuminate\Validation\ValidationException als de naam ontbreekt
     */
    public function update(Request $request, $id)
    {
        $data = $request->validate(['name' => 'required|string']);

        $response = $this->api()->put('/authors/' . $id, $data);

        if ($response->failed()) {
            return $this->error($response, 'Auteur wijzigen mislukt.');
        }

        return back()->with('success', 'Auteur gewijzigd.');
    }

    /**
     * Verwijdert een auteur via DELETE /authors/{id} van de API.
     *
     * Route: DELETE /admin/authors/{id}
     *
     * @param  int|string  $id  id van de auteur
     * @return \Illuminate\Http\RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     */
    public function destroy($id)
    {
        $response = $this->api()->delete('/authors/' . $id);

        if ($response->failed()) {
            return $this->error($response, 'Auteur verwijderen mislukt.');
        }

        return back()->with('success', 'Auteur verwijderd.');
    }
}

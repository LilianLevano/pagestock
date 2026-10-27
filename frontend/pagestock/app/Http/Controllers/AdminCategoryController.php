<?php

namespace App\Http\Controllers;

use Illuminate\Http\Client\ConnectionException;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;
use Illuminate\View\View;
use Symfony\Component\HttpKernel\Exception\HttpException;

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
     * Toont alle categorieën, met de formulieren om toe te voegen, te wijzigen en te verwijderen.
     *
     * Route: GET /admin/categories
     *
     * @return View de view admin.categories.categories met $items
     *
     * @throws HttpException|ConnectionException met de statuscode van de API als die faalt
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
     * @return RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws ValidationException|ConnectionException als de naam ontbreekt
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
     * @return RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     *
     * @throws ValidationException|ConnectionException als de naam ontbreekt
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
     * @param int|string $id id van de categorie
     * @return RedirectResponse terug naar de pagina, met een succesmelding of zie {@see error()}
     * @throws ConnectionException
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

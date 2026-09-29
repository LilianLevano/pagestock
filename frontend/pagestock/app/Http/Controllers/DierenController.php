<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use function Pest\Laravel\get;

class DierenController extends Controller
{
    public function index()
    {
        $response = Http::get(config('services.backend.url').'/api/dieren');
        $dieren = $response->json();

        return view('dieren', compact('dieren'));

    }
}

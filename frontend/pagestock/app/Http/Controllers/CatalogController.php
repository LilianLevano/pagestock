<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;


class CatalogController extends Controller
{
    public function index(){


        $response = Http::get(config("services.backend.url" ) . "/books");
        $books = $response->json();

        return view('welcome', compact('books'));
    }
}

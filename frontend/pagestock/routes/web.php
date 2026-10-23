<?php

use App\Http\Controllers\AuthController;
use App\Http\Controllers\ReviewController;
use App\Http\Middleware\EnsureApiToken;
use Illuminate\Support\Facades\Route;

Route::get('/', [\App\Http\Controllers\CatalogController::class, 'index'])->name('catalog.index');
Route::get('/books/{id}', [\App\Http\Controllers\CatalogController::class, 'show'])->name('catalog.show');

// Formulieren tonen (GET) en verwerken (POST)
Route::get('/login', [AuthController::class, 'showLogin'])->name('login');
Route::post('/login', [AuthController::class, 'login']);
Route::get('/register', [AuthController::class, 'showRegister'])->name('register');
Route::post('/register', [AuthController::class, 'register']);

// Uitloggen via POST (met @csrf), zodat een simpele link niemand kan uitloggen
Route::post('/logout', [AuthController::class, 'logout'])->name('logout');

// Beschermde routes in deze groep zetten (winkelmandje, reservaties, profiel)
Route::middleware(EnsureApiToken::class)->group(function () {
    Route::get('/books/{isbn}/reviews/create', [ReviewController::class, 'create'])->name('reviews.create');
    Route::post('/books/{isbn}/reviews', [ReviewController::class, 'store'])->name('reviews.store');
});

<?php

use App\Http\Controllers\AdminReservationController;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\CartController;
use App\Http\Controllers\ReservationController;
use App\Http\Controllers\ReviewController;
use App\Http\Middleware\EnsureAdmin;
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

    // Winkelmandje
    Route::get('/cart', [CartController::class, 'index'])->name('cart.index');
    Route::post('/cart/add/{isbn}', [CartController::class, 'add'])->name('cart.add');
    Route::post('/cart/remove/{isbn}', [CartController::class, 'remove'])->name('cart.remove');

    // Reservaties van de gebruiker
    Route::get('/reservations', [ReservationController::class, 'index'])->name('reservations.index');
    Route::post('/reservations', [ReservationController::class, 'store'])->name('reservations.store');
    Route::patch('/reservations/{id}/cancel', [ReservationController::class, 'cancel'])->name('reservations.cancel');
    Route::get('/my-books', [ReservationController::class, 'purchased'])->name('reservations.purchased');

    // Admin: ingelogd EN admin
    Route::middleware(EnsureAdmin::class)->prefix('admin')->group(function () {
        Route::get('/reservations', [AdminReservationController::class, 'index'])->name('admin.reservations');
        Route::patch('/reservations/{id}/pickup', [AdminReservationController::class, 'pickup'])->name('admin.reservations.pickup');
    });


});

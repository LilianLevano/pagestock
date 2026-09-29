<?php

use App\Http\Controllers\DierenController;
use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return view('welcome');
});

Route::get('/dieren', [DierenController::class, 'index']);

<?php

use Illuminate\Support\Facades\Route;

Route::get('/', [\App\Http\Controllers\CatalogController::class, 'index'])->name('catalog.index');
Route::get('/{id}', [\App\Http\Controllers\CatalogController::class, 'show'])->name('catalog.show');

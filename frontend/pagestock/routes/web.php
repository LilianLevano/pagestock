<?php

use Illuminate\Support\Facades\Route;

Route::get('/', [\App\Http\Controllers\CatalogController::class, 'index'])->name('catalog.index');

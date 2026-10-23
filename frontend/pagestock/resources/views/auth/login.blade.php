<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Login</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>

<div class="max-w-md mx-auto px-4 py-12">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Inloggen</h1>

    {{-- Succesbericht na registratie (komt uit ->with('success', ...)) --}}
    @if(session('success'))
        <p class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">{{ session('success') }}</p>
    @endif

    <form method="POST" action="{{ route('login') }}" class="bg-white border rounded-lg shadow-sm p-6 space-y-4">
        {{-- Verplicht in Laravel: beschermt tegen CSRF-aanvallen --}}
        @csrf

        <div>
            <label for="email" class="block text-sm text-gray-700">E-mailadres</label>
            {{-- old('email') vult het veld opnieuw in na een fout --}}
            <input type="email" id="email" name="email" value="{{ old('email') }}"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            {{-- Foutmelding voor dit veld --}}
            @error('email')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        <div>
            <label for="password" class="block text-sm text-gray-700">Wachtwoord</label>
            <input type="password" id="password" name="password"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            @error('password')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        <button type="submit" class="w-full bg-gray-900 text-white py-2 rounded-lg hover:bg-gray-700">
            Inloggen
        </button>
    </form>

    <p class="mt-4 text-sm text-gray-600">
        Nog geen account? <a href="{{ route('register') }}" class="underline">Registreer hier</a>
    </p>
</div>
</body>
</html>

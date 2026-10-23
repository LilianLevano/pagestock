<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Register</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body>

<div class="max-w-md mx-auto px-4 py-12">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Registreren</h1>

    <form method="POST" action="{{ route('register') }}" class="bg-white border rounded-lg shadow-sm p-6 space-y-4">
        {{-- Verplicht in Laravel: beschermt tegen CSRF-aanvallen --}}
        @csrf

        {{-- Voornaam en achternaam naast elkaar --}}
        <div class="grid grid-cols-2 gap-4">
            <div>
                <label for="firstName" class="block text-sm text-gray-700">Voornaam</label>
                <input type="text" id="firstName" name="firstName" value="{{ old('firstName') }}"
                       class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                @error('firstName')
                <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
                @enderror
            </div>
            <div>
                <label for="lastName" class="block text-sm text-gray-700">Achternaam</label>
                <input type="text" id="lastName" name="lastName" value="{{ old('lastName') }}"
                       class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                @error('lastName')
                <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
                @enderror
            </div>
        </div>

        <div>
            <label for="email" class="block text-sm text-gray-700">E-mailadres</label>
            <input type="email" id="email" name="email" value="{{ old('email') }}"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            @error('email')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        <div>
            <label for="phoneNumber" class="block text-sm text-gray-700">Telefoonnummer</label>
            <input type="text" id="phoneNumber" name="phoneNumber" value="{{ old('phoneNumber') }}"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            @error('phoneNumber')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        {{-- Wachtwoordvelden krijgen nooit old(): een wachtwoord sturen we niet terug naar de browser --}}
        <div>
            <label for="password" class="block text-sm text-gray-700">Wachtwoord (min. 8 tekens)</label>
            <input type="password" id="password" name="password"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            @error('password')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        <div>
            <label for="confirmPassword" class="block text-sm text-gray-700">Bevestig wachtwoord</label>
            <input type="password" id="confirmPassword" name="confirmPassword"
                   class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
            @error('confirmPassword')
            <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
            @enderror
        </div>

        <button type="submit" class="w-full bg-gray-900 text-white py-2 rounded-lg hover:bg-gray-700">
            Account aanmaken
        </button>
    </form>

    <p class="mt-4 text-sm text-gray-600">
        Al een account? <a href="{{ route('login') }}" class="underline">Log hier in</a>
    </p>
</div>
</body>
</html>

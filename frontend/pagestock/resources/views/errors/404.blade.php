<x-site-layout>
    <div class="max-w-2xl mx-auto px-4 py-16 text-center">
        <h1 class="text-3xl font-bold text-gray-900">Niet gevonden</h1>
        {{-- $exception bevat de boodschap die we aan abort() meegaven --}}
        <p class="text-gray-600 mt-4">{{ $exception->getMessage() ?: 'Deze pagina bestaat niet.' }}</p>
        <a href="{{ route('catalog.index') }}" class="inline-block mt-6 underline">Terug naar de catalogus</a>
    </div>
</x-site-layout>

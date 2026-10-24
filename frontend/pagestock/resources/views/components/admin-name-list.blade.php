{{-- Component voor auteurs én categorieën: beide hebben enkel een id en een naam --}}
{{-- $route = begin van de routenamen, bv. 'admin.authors' --}}
@props(['title', 'items', 'route'])

<x-site-layout>
    <div class="max-w-3xl mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">{{ $title }}</h1>

        <x-flash />

        {{-- Toevoegen --}}
        <form method="POST" action="{{ route($route . '.store') }}" class="flex gap-2 mb-6">
            @csrf
            <input type="text" name="name" placeholder="Naam" class="flex-1 border border-gray-300 rounded-lg px-3 py-2">
            <button type="submit" class="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-700">Toevoegen</button>
        </form>

        {{-- Lijst: elke rij is meteen een formulier om de naam te wijzigen --}}
        <div class="bg-white border rounded-lg shadow-sm divide-y">
            @forelse($items as $item)
                <div class="flex items-center gap-2 p-3">
                    <form method="POST" action="{{ route($route . '.update', $item['id']) }}" class="flex flex-1 gap-2">
                        @csrf
                        @method('PUT')
                        <input type="text" name="name" value="{{ $item['name'] }}" class="flex-1 border border-gray-300 rounded-lg px-3 py-1">
                        <button type="submit" class="text-gray-600 hover:underline text-sm">Opslaan</button>
                    </form>
                    <form method="POST" action="{{ route($route . '.destroy', $item['id']) }}"
                          onsubmit="return confirm('Verwijderen?')">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="text-red-600 hover:underline text-sm">Verwijderen</button>
                    </form>
                </div>
            @empty
                <p class="p-3 text-gray-500">Nog niets toegevoegd.</p>
            @endforelse
        </div>
    </div>
</x-site-layout>

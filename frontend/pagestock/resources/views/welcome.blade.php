<x-site-layout>
    <div class="max-w-7xl mx-auto px-4 py-8">

        {{-- Zoekbalk: GET-formulier, de zoekterm komt in de URL (?search=...) --}}
        <form method="GET" action="{{ url('/') }}" class="mb-8 flex gap-2">
            <input type="text" name="search" value="{{ request('search') }}"
                   placeholder="Zoek een boek op titel..."
                   class="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-gray-400">
            <button type="submit" class="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-700">
                Zoeken
            </button>
        </form>

        {{-- Grid: 1 kolom op gsm, 2 op tablet, 4 op groot scherm --}}
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {{-- @forelse = @foreach met een @empty-blok als de lijst leeg is --}}
            @forelse($books as $book)
                {{-- Eén kaart per boek --}}
                <x-book-card :book="$book" />
            @empty
                {{-- Wordt getoond als er geen boeken zijn (of de zoekopdracht niets vindt) --}}
                <p class="text-gray-500 col-span-full">Geen boeken gevonden.</p>
            @endforelse
        </div>
    </div>
</x-site-layout>

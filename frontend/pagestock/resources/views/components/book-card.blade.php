{{-- Props: het component verwacht één boek (array uit de API) --}}
@props(['book'])

{{-- Eén kaart per boek --}}
<div class="bg-white border rounded-lg shadow-sm p-4 flex flex-col">
    <h2 class="text-lg font-semibold text-gray-900">{{ $book['title'] }}</h2>
    <p class="text-sm text-gray-500 mt-1">ISBN: {{ $book['isbnNumber'] }}</p>

    {{-- mt-auto duwt prijs en knoppen naar onderen, zodat alle kaarten gelijk uitlijnen --}}
    <div class="mt-auto pt-4">
        <p class="text-lg font-bold text-gray-900">€ {{ number_format($book['price'], 2, ',', '.') }}</p>

        {{-- Voorraad tonen: rood als er niets meer is --}}
        @if($book['stock'] > 0)
            <p class="text-sm text-green-600">{{ $book['stock'] }} op voorraad</p>
        @else
            <p class="text-sm text-red-600">Niet op voorraad</p>
        @endif

        {{-- Knoppen: voorlopig nog zonder functie --}}
        <div class="flex gap-2 mt-3">
            <a href="{{route("catalog.show", $book['isbnNumber'])}}" class="flex-1 text-center border border-gray-900 text-gray-900 px-3 py-2 rounded-lg hover:bg-gray-100">
                Details
            </a>
            {{-- Formulier: voegt één exemplaar toe aan het winkelmandje in de sessie --}}
            <form method="POST" action="{{ route('cart.add', $book['isbnNumber']) }}">
                @csrf
                <button type="submit" class="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-700"
                        aria-label="Toevoegen aan winkelmandje" @disabled($book['stock'] < 1)>
                    +
                </button>
            </form>
        </div>
    </div>
</div>

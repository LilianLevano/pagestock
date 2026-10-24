<x-site-layout>
    <div class="max-w-3xl mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">Winkelmandje</h1>

        <x-flash />

        @if($items->isEmpty())
            <p class="text-gray-500">Je winkelmandje is leeg.</p>
        @else
            <div class="bg-white border rounded-lg shadow-sm divide-y">
                @foreach($items as $item)
                    {{-- Eén lijn per boek: titel, aantal, prijs en verwijderknop --}}
                    <div class="flex items-center justify-between p-4">
                        <div>
                            <p class="font-semibold text-gray-900">{{ $item['title'] }}</p>
                            <p class="text-sm text-gray-500">
                                {{ $item['quantity'] }} × € {{ number_format($item['price'], 2, ',', '.') }}
                            </p>
                        </div>
                        <form method="POST" action="{{ route('cart.remove', $item['isbn']) }}">
                            @csrf
                            <button type="submit" class="text-sm text-red-600 hover:underline">Verwijderen</button>
                        </form>
                    </div>
                @endforeach
            </div>

            {{-- Totaal + knop om het mandje om te zetten naar een reservatie --}}
            <div class="flex items-center justify-between mt-6">
                <p class="text-xl font-bold text-gray-900">Totaal: € {{ number_format($total, 2, ',', '.') }}</p>
                <form method="POST" action="{{ route('reservations.store') }}">
                    @csrf
                    <button type="submit" class="bg-gray-900 text-white px-6 py-2 rounded-lg hover:bg-gray-700">
                        Reserveren
                    </button>
                </form>
            </div>
            <p class="text-sm text-gray-500 mt-2">Je betaalt bij het afhalen in de winkel.</p>
        @endif
    </div>
</x-site-layout>

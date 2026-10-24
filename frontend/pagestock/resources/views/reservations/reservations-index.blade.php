<x-site-layout>
    <div class="max-w-3xl mx-auto px-4 py-8">
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-3xl font-bold text-gray-900">Mijn reservaties</h1>
            <a href="{{ route('reservations.purchased') }}" class="text-gray-600 hover:text-gray-900">Mijn boeken &rarr;</a>
        </div>

        <x-flash />

        <div class="space-y-4">
            @forelse($reservations as $reservation)
                <div class="bg-white border rounded-lg shadow-sm p-4">
                    {{-- Kop: nummer, datum en status --}}
                    <div class="flex items-center justify-between">
                        <div>
                            <p class="font-semibold text-gray-900">Reservatie #{{ $reservation['id'] }}</p>
                            {{-- Carbon zet de datum uit de API om naar een leesbaar formaat --}}
                            <p class="text-sm text-gray-500">
                                {{ \Carbon\Carbon::parse($reservation['reservedAt'])->format('d/m/Y H:i') }}
                            </p>
                        </div>
                        <x-reservation-status :status="$reservation['status']" />
                    </div>

                    {{-- Lijnen van de reservatie --}}
                    <ul class="mt-3 text-sm text-gray-700 list-disc list-inside">
                        @foreach($reservation['lines'] as $line)
                            <li>{{ $line['quantity'] }} × {{ $line['bookTitle'] ?? $line['bookIsbn'] }}</li>
                        @endforeach
                    </ul>

                    {{-- Annuleren kan enkel zolang de reservatie nog niet afgehaald is --}}
                    @if($reservation['status'] === 'RESERVED')
                        <form method="POST" action="{{ route('reservations.cancel', $reservation['id']) }}" class="mt-3">
                            @csrf
                            {{-- HTML-formulieren kennen enkel GET en POST: @method zegt Laravel dat het een PATCH is --}}
                            @method('PATCH')
                            <button type="submit" class="text-sm text-red-600 hover:underline">Annuleren</button>
                        </form>
                    @endif
                </div>
            @empty
                <p class="text-gray-500">Je hebt nog geen reservaties.</p>
            @endforelse
        </div>
    </div>
</x-site-layout>

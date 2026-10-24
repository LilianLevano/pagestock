<x-site-layout>
    <div class="max-w-5xl mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">Alle reservaties (admin)</h1>

        <x-flash />

        {{-- Tabel: overflow-x-auto laat horizontaal scrollen op een smal scherm --}}
        <div class="bg-white border rounded-lg shadow-sm overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-left text-gray-500">
                <tr>
                    <th class="p-3">#</th>
                    <th class="p-3">Datum</th>
                    <th class="p-3">Boeken</th>
                    <th class="p-3">Status</th>
                    <th class="p-3"></th>
                </tr>
                </thead>
                <tbody class="divide-y">
                @forelse($reservations as $reservation)
                    <tr>
                        <td class="p-3">{{ $reservation['id'] }}</td>
                        <td class="p-3">{{ \Carbon\Carbon::parse($reservation['reservedAt'])->format('d/m/Y H:i') }}</td>
                        <td class="p-3">
                            @foreach($reservation['lines'] as $line)
                                <div>{{ $line['quantity'] }} × {{ $line['bookTitle'] ?? $line['bookIsbn'] }}</div>
                            @endforeach
                        </td>
                        <td class="p-3"><x-reservation-status :status="$reservation['status']" /></td>
                        <td class="p-3 text-right">
                            {{-- Afhaling bevestigen: alleen voor reservaties die nog openstaan --}}
                            @if($reservation['status'] === 'RESERVED')
                                <form method="POST" action="{{ route('admin.reservations.pickup', $reservation['id']) }}">
                                    @csrf
                                    @method('PATCH')
                                    <button type="submit" class="bg-gray-900 text-white px-3 py-1 rounded-lg hover:bg-gray-700">
                                        Afgehaald
                                    </button>
                                </form>
                            @endif
                            @if($reservation['status'] === 'RESERVED')
                                <form method="POST" action="{{ route('reservations.cancel', $reservation['id']) }}" class="mt-3">
                                    @csrf
                                    {{-- HTML-formulieren kennen enkel GET en POST: @method zegt Laravel dat het een PATCH is --}}
                                    @method('PATCH')
                                    <button type="submit" class="text-sm text-red-600 hover:underline">Annuleren</button>
                                </form>
                            @endif
                        </td>
                    </tr>
                @empty
                    <tr><td colspan="5" class="p-3 text-gray-500">Nog geen reservaties.</td></tr>
                @endforelse
                </tbody>
            </table>
        </div>
    </div>
</x-site-layout>

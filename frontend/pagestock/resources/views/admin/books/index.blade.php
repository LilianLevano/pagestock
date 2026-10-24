<x-site-layout>
    <div class="max-w-6xl mx-auto px-4 py-8">
        {{-- Titel + knop om een nieuw boek toe te voegen --}}
        <div class="flex items-center justify-between mb-6">
            <h1 class="text-3xl font-bold text-gray-900">Boeken beheren</h1>
            <a href="{{ route('admin.books.create') }}" class="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-700">
                + Nieuw boek
            </a>
        </div>

        <x-flash />

        {{-- Tabel: overflow-x-auto laat horizontaal scrollen op een smal scherm --}}
        <div class="bg-white border rounded-lg shadow-sm overflow-x-auto">
            <table class="w-full text-sm">
                <thead class="bg-gray-50 text-left text-gray-500">
                <tr>
                    <th class="p-3">ISBN</th>
                    <th class="p-3">Titel</th>
                    <th class="p-3">Categorie</th>
                    <th class="p-3">Prijs</th>
                    <th class="p-3">Voorraad</th>
                    <th class="p-3"></th>
                </tr>
                </thead>
                <tbody class="divide-y">
                @forelse($books as $book)
                    <tr>
                        <td class="p-3">{{ $book['isbnNumber'] }}</td>
                        <td class="p-3 font-semibold">{{ $book['title'] }}</td>
                        <td class="p-3">{{ $book['category']['name'] ?? '-' }}</td>
                        <td class="p-3">€ {{ number_format($book['price'], 2, ',', '.') }}</td>
                        <td class="p-3">{{ $book['stock'] }}</td>
                        <td class="p-3">
                            <div class="flex justify-end gap-3">
                                <a href="{{ route('admin.books.edit', $book['isbnNumber']) }}" class="text-gray-600 hover:underline">Wijzigen</a>
                                {{-- Verwijderen via DELETE; confirm() vraagt eerst bevestiging in de browser --}}
                                <form method="POST" action="{{ route('admin.books.destroy', $book['isbnNumber']) }}"
                                      onsubmit="return confirm('Dit boek verwijderen?')">
                                    @csrf
                                    @method('DELETE')
                                    <button type="submit" class="text-red-600 hover:underline">Verwijderen</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                @empty
                    <tr><td colspan="6" class="p-3 text-gray-500">Nog geen boeken.</td></tr>
                @endforelse
                </tbody>
            </table>
        </div>
    </div>
</x-site-layout>

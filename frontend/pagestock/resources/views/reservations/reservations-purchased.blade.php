<x-site-layout>
    <div class="max-w-3xl mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-6">Mijn boeken</h1>

        {{-- Boeken die al afgehaald zijn: hierover mag de gebruiker een review schrijven --}}
        <div class="bg-white border rounded-lg shadow-sm divide-y">
            @forelse($books as $book)


                <div class="flex items-center justify-between p-4">
                    <p class="font-semibold text-gray-900">{{ $book['bookTitle'] ?? $book['title'] ?? $book['isbnNumber'] }}</p>
                    <a href="{{ route('reviews.create', $book['bookIsbn']) }}" class="text-sm text-gray-600 hover:underline">
                        Review schrijven
                    </a>
                </div>
            @empty
                <p class="p-4 text-gray-500">Je hebt nog geen boeken afgehaald.</p>
            @endforelse
        </div>
    </div>
</x-site-layout>

<x-site-layout>
    <div class="max-w-7xl mx-auto px-4 py-8">

        {{-- Titel bovenaan de pagina --}}
        <h1 class="text-3xl font-bold text-gray-900">{{ $book['title'] }} - Detail</h1>

        {{-- Terugknop naar de catalogus --}}
        <a href="{{ route('catalog.index') }}" class="inline-block mt-4 text-gray-600 hover:text-gray-900">
            &larr; Terug naar catalogus
        </a>

        {{-- Twee kolommen: onder elkaar op gsm, naast elkaar vanaf md --}}
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mt-8">

            {{-- LINKS: alle informatie over het boek --}}
            <section class="bg-white border rounded-lg shadow-sm p-6">
                <h2 class="text-xl font-semibold text-gray-900 mb-4">Boekinformatie</h2>

                {{-- dl/dt/dd = lijst van label + waarde --}}
                <dl class="space-y-3">
                    <div>
                        <dt class="text-sm text-gray-500">Titel</dt>
                        <dd class="text-gray-900">{{ $book['title'] }}</dd>
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">ISBN</dt>
                        <dd class="text-gray-900">{{ $book['isbnNumber'] }}</dd>
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">Auteur(s)</dt>
                        {{-- Auteurs zijn een lijst: elk object omzetten naar een naam en scheiden met komma's --}}
                        <dd class="text-gray-900">
                            {{ collect($book['authors'] ?? [])->map(fn ($author) => trim(($author['name'] ?? '')))->implode(', ') }}
                        </dd>
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">Categorie</dt>
                        {{-- ?? vermijdt een fout als er geen categorie is --}}
                        <dd class="text-gray-900">{{ $book['category']['name'] ?? '-' }}</dd>
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">Prijs</dt>
                        <dd class="text-gray-900 font-bold">€ {{ number_format($book['price'], 2, ',', '.') }}</dd>
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">Voorraad</dt>
                        {{-- Rood als het boek niet op voorraad is --}}
                        @if($book['stock'] > 0)
                            <dd class="text-green-600">{{ $book['stock'] }} op voorraad</dd>
                        @else
                            <dd class="text-red-600">Niet op voorraad</dd>
                        @endif
                    </div>
                    <div>
                        <dt class="text-sm text-gray-500">Beschrijving</dt>
                        <dd class="text-gray-900">{{ $book['description'] ?? '-' }}</dd>
                    </div>
                </dl>
            </section>

            {{-- RECHTS: reviews van dit boek --}}
            <section class="bg-white border rounded-lg shadow-sm p-6">
                {{-- Kop met knop om een review toe te voegen (voorlopig zonder functie) --}}
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-xl font-semibold text-gray-900">Reviews</h2>
                    <button type="button" class="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-700">
                        Review toevoegen
                    </button>
                </div>

                {{-- Lijst van reviews, met een melding als er nog geen zijn --}}
                <div class="space-y-4">
                    @forelse($reviews as $review)
                        <div class="border-b pb-4 last:border-b-0">
                            <div class="flex items-center justify-between">
                                <p class="font-semibold text-gray-900">{{ $review['authorReviewName'] }}</p>
                                {{-- Score als sterren: volle sterren voor de score, lege voor de rest (op 5) --}}
                                <p class="text-yellow-500">
                                    {{ str_repeat('★', $review['rating']) }}{{ str_repeat('☆', 5 - $review['rating']) }}
                                </p>
                            </div>
                            <p class="text-gray-700 mt-1">{{ $review['comment'] }}</p>
                            <p class="text-xs text-gray-400 mt-1">{{ $review['createdAt'] }}</p>
                        </div>
                    @empty
                        <p class="text-gray-500">Nog geen reviews voor dit boek.</p>
                    @endforelse
                </div>
            </section>
        </div>
    </div>
</x-site-layout>

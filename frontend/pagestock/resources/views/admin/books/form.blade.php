{{-- Eén formulier voor toevoegen ($book = null) én wijzigen ($book = bestaand boek) --}}
<x-site-layout>
    <div class="max-w-2xl mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold text-gray-900">
            {{ $book ? $book['title'] . ' - Wijzigen' : 'Nieuw boek' }}
        </h1>

        <a href="{{ route('admin.books.index') }}" class="inline-block mt-4 text-gray-600 hover:text-gray-900">
            &larr; Terug naar de lijst
        </a>

        <div class="mt-6"><x-flash /></div>

        {{-- Zelfde formulier, andere route: store (POST) of update (PUT) --}}
        <form method="POST"
              action="{{ $book ? route('admin.books.update', $book['isbnNumber']) : route('admin.books.store') }}"
              class="bg-white border rounded-lg shadow-sm p-6 space-y-4">
            @csrf
            @if($book)
                @method('PUT')
            @endif

            {{-- ISBN enkel bij een nieuw boek: het is de primaire sleutel en kan niet gewijzigd worden --}}
            @unless($book)
                <div>
                    <label for="isbnNumber" class="block text-sm text-gray-700">ISBN (10 of 13 tekens)</label>
                    <input type="text" id="isbnNumber" name="isbnNumber" value="{{ old('isbnNumber') }}"
                           class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                    @error('isbnNumber') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
                </div>
            @endunless

            <div>
                <label for="title" class="block text-sm text-gray-700">Titel</label>
                {{-- old('veld', standaardwaarde): na een fout de ingevulde waarde, anders de waarde van het boek --}}
                <input type="text" id="title" name="title" value="{{ old('title', $book['title'] ?? '') }}"
                       class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                @error('title') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
            </div>

            <div>
                <label for="description" class="block text-sm text-gray-700">Beschrijving</label>
                <textarea id="description" name="description" rows="4"
                          class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">{{ old('description', $book['description'] ?? '') }}</textarea>
                @error('description') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
            </div>

            {{-- Prijs, voorraad en publicatiedatum naast elkaar --}}
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                    <label for="price" class="block text-sm text-gray-700">Prijs (€)</label>
                    <input type="number" step="0.01" min="0" id="price" name="price" value="{{ old('price', $book['price'] ?? '') }}"
                           class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                    @error('price') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
                </div>
                <div>
                    <label for="stock" class="block text-sm text-gray-700">Voorraad</label>
                    <input type="number" min="0" id="stock" name="stock" value="{{ old('stock', $book['stock'] ?? '') }}"
                           class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                    @error('stock') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
                </div>
                <div>
                    <label for="publicationDate" class="block text-sm text-gray-700">Publicatiedatum</label>
                    <input type="date" id="publicationDate" name="publicationDate"
                           value="{{ old('publicationDate', $book['publicationDate'] ?? '') }}"
                           class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                    @error('publicationDate') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
                </div>
            </div>

            {{-- Categorie: één keuze uit de lijst van de API --}}
            <div>
                <label for="categoryId" class="block text-sm text-gray-700">Categorie</label>
                <select id="categoryId" name="categoryId" class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">
                    <option value="">-- Kies een categorie --</option>
                    @foreach($categories as $category)
                        <option value="{{ $category['id'] }}"
                            @selected(old('categoryId', $book['category']['id'] ?? null) == $category['id'])>
                            {{ $category['name'] }}
                        </option>
                    @endforeach
                </select>
                @error('categoryId') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
            </div>

            {{-- Auteurs: meerdere keuzes mogelijk, daarom checkboxes met name="authorIds[]" (= array) --}}
            @php
                // Id's van de auteurs die aangevinkt moeten zijn: na een fout old(), anders de auteurs van het boek
                $selectedAuthors = old('authorIds', collect($book['authors'] ?? [])->pluck('id')->all());
            @endphp
            <fieldset>
                <legend class="block text-sm text-gray-700 mb-2">Auteur(s)</legend>
                <div class="grid grid-cols-2 gap-2 max-h-48 overflow-y-auto border border-gray-300 rounded-lg p-3">
                    @foreach($authors as $author)
                        <label class="flex items-center gap-2">
                            <input type="checkbox" name="authorIds[]" value="{{ $author['id'] }}"
                                @checked(in_array($author['id'], $selectedAuthors))>
                            {{ $author['name'] }}
                        </label>
                    @endforeach
                </div>
                @error('authorIds') <p class="text-sm text-red-600 mt-1">{{ $message }}</p> @enderror
            </fieldset>

            <button type="submit" class="w-full bg-gray-900 text-white py-2 rounded-lg hover:bg-gray-700">
                {{ $book ? 'Wijzigingen opslaan' : 'Boek toevoegen' }}
            </button>
        </form>
    </div>
</x-site-layout>

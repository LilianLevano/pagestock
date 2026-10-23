<x-site-layout>
    <div class="max-w-2xl mx-auto px-4 py-8">

        <h1 class="text-3xl font-bold text-gray-900">{{ $book['title'] }} - Review schrijven</h1>

        {{-- Terugknop naar de detailpagina van het boek --}}
        <a href="{{ route('catalog.show', $book['isbnNumber']) }}" class="inline-block mt-4 text-gray-600 hover:text-gray-900">
            &larr; Terug naar het boek
        </a>

        <form method="POST" action="{{ route('reviews.store', $book['isbnNumber']) }}"
              class="bg-white border rounded-lg shadow-sm p-6 space-y-6 mt-8">
            {{-- Verplicht in Laravel: beschermt tegen CSRF-aanvallen --}}
            @csrf

            {{-- Score: 5 radioknoppen, je kiest er precies één --}}
            <fieldset>
                <legend class="block text-sm text-gray-700 mb-2">Score</legend>
                <div class="flex gap-4">
                    @for($i = 1; $i <= 5; $i++)
                        <label class="flex items-center gap-1 cursor-pointer">
                            {{-- old('rating') houdt de keuze bij na een fout --}}
                            <input type="radio" name="rating" value="{{ $i }}" @checked(old('rating') == $i)>
                            <span class="text-yellow-500">{{ str_repeat('★', $i) }}</span>
                        </label>
                    @endfor
                </div>
                @error('rating')
                <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
                @enderror
            </fieldset>

            {{-- Tekst van de review --}}
            <div>
                <label for="comment" class="block text-sm text-gray-700">Je review</label>
                <textarea id="comment" name="comment" rows="6" maxlength="2000"
                          class="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2">{{ old('comment') }}</textarea>
                @error('comment')
                <p class="text-sm text-red-600 mt-1">{{ $message }}</p>
                @enderror
            </div>

            <button type="submit" class="w-full bg-gray-900 text-white py-2 rounded-lg hover:bg-gray-700">
                Review plaatsen
            </button>
        </form>
    </div>
</x-site-layout>

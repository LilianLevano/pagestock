<header class="bg-white border-b shadow-sm">
    {{-- Flex-container: links het logo + menu, rechts de iconen --}}
    <nav class="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">

        {{-- Linkerkant: naam van de winkel + navigatielinks --}}
        <div class="flex items-center gap-8">
            <a href="{{ url('/') }}" class="text-xl font-bold text-gray-900">PageStock</a>
            <x-nav-bar-element url="/" label="Home" />
            @if(session()->has('token'))
                <x-nav-bar-element url="/reservations" label="Mijn reservaties" />
            @endif
            @if(session('role') === 'ADMIN')
                <x-nav-bar-element url="/admin/reservations" label="Admin" />
            @endif


        </div>

        {{-- Rechterkant: winkelmandje en gebruikersicoon --}}
        <div class="flex items-center gap-4">
            {{-- Winkelmandje-icoon (Heroicons shopping-cart) --}}
            <a href="{{ route('cart.index') }}" class="text-gray-600 hover:text-gray-900" aria-label="Winkelmandje">
                <svg fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272M6 20.25a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Zm12.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" />
                </svg>
            </a>
            {{-- Adminmenu in de navigatie: enkel zichtbaar voor admins --}}
            @if(session('role') === 'ADMIN')
                {{-- details/summary = uitklapmenu in pure HTML, zonder JavaScript --}}
                <details class="relative">
                    <summary class="list-none cursor-pointer text-sm text-gray-600 hover:text-gray-900 select-none">
                        Admin &#9662;
                    </summary>
                    {{-- absolute: het menu zweeft onder de knop zonder de navigatie te verschuiven --}}
                    <div class="absolute right-0 mt-2 w-48 bg-white border rounded-lg shadow-lg z-10 py-2">
                        <a href="{{ route('admin.books.index') }}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">Boeken</a>
                        <a href="{{ route('admin.authors.index') }}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">Auteurs</a>
                        <a href="{{ route('admin.categories.index') }}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">Categorieën</a>
                        <a href="{{ route('admin.reservations') }}" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">Reservaties</a>
                    </div>
                </details>
            @endif

            @if(session()->has('token'))
                {{-- Ingelogd: uitloggen via een POST-formulier (met @csrf) --}}
                <form method="POST" action="{{ route('logout') }}">
                    @csrf
                    <button type="submit" class="text-gray-600 hover:text-gray-900 text-sm">Uitloggen</button>
                </form>
            @else


                    <a href="{{ route('login') }}" class="text-gray-600 hover:text-gray-900 flex flex-row gap-2.5" aria-label="Profiel">
                        <p>Inloggen</p>
                        <svg fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-7 h-7">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 1 1 6 0Z" />
                        </svg>
                    </a>

            @endif

        </div>
    </nav>
</header>



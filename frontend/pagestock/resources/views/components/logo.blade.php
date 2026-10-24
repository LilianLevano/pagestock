{{-- Logo van PageStock, herbruikbaar op elke pagina --}}
{{-- asset() maakt de juiste URL naar een bestand in de map public/ --}}
{{-- $attributes->merge(): klassen van buitenaf (bv. grootte, marges) worden toegevoegd aan de standaardklassen --}}
<img src="{{ asset('logo.png') }}" alt="PageStock logo" {{ $attributes->merge(['class' => 'h-50 w-auto']) }}>

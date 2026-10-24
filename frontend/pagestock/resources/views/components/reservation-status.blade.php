{{-- Component: gekleurd label voor de status van een reservatie --}}
@props(['status'])

@php
    // match = moderne switch in PHP: elke status krijgt een kleur en een tekst
    [$classes, $label] = match ($status) {
        'RESERVED' => ['bg-yellow-100 text-yellow-800', 'Gereserveerd'],
        'PICKED_UP' => ['bg-green-100 text-green-800', 'Afgehaald'],
        'CANCELLED' => ['bg-gray-200 text-gray-600', 'Geannuleerd'],
        default => ['bg-gray-100 text-gray-600', $status],
    };
@endphp

<span class="text-xs font-semibold px-2 py-1 rounded {{ $classes }}">{{ $label }}</span>

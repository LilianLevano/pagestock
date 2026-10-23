<a href="{{ $url }}" class="text-gray-600 hover:text-gray-900 {{ request()->is(ltrim($url, '/') ?: '/') ? 'underline underline-offset-8 decoration-2' : '' }}">{{$label}}</a>

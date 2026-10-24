@if(session('success'))
    <p class="mb-4 p-3 rounded-lg bg-green-100 text-green-700">{{ session('success') }}</p>
@endif

{{-- $errors bevat de fouten van withErrors([...]) --}}
@if($errors->any())
    <div class="mb-4 p-3 rounded-lg bg-red-100 text-red-700">
        @foreach($errors->all() as $error)
            <p>{{ $error }}</p>
        @endforeach
    </div>
@endif

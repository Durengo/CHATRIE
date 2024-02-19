<script>
    import { isLoggedIn } from '$lib/stores.js';
    import { goto } from '$app/navigation';

    if (!$isLoggedIn) {
        goto('/login'); 
    }
</script>

<h1>Your Profile</h1>

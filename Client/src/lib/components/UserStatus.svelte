<script>
    import { isLoggedIn, user } from '$lib/stores.js';

    function handleLogout() {
        $isLoggedIn = false;
        $user = null;

        // Optional: Redirect to the login page after logout
        goto('/login');
    }
</script>

{#if $isLoggedIn}
    <p>Welcome, {$user.nickname}!</p>
    <button on:click={handleLogout}>Logout</button>
{:else}
    <p>Please log in.</p>
{/if}

<script>
    import { isLoggedIn, user } from '$lib/stores.js';

    let nickname = '';
    let password = '';

    async function handleSubmit() {
        // In a real app, send credentials to the backend for validation
        const response = await fetch('/api/login', { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nickname, password })
        });

        if (response.ok) {
            // Simulated successful login
            $isLoggedIn = true;
            $user = { nickname }; 

            // Redirect to profile after login (adjust if needed)
            goto('/profile'); 
        } else {
            // Handle login error (display a message, etc.)
            alert('Login failed');  
        }
    }
</script>

<form on:submit|preventDefault={handleSubmit}>
    <input type="text" bind:value={nickname} placeholder="Nickname">
    <input type="password" bind:value={password} placeholder="Password">
    <button type="submit">Login</button>
</form>

<script>
	import { onMount } from 'svelte';
	import { apiBaseUrl } from '../stores.js';

	let username = '';
	let password = '';

	async function handleSubmit() {
		try {
			const response = await fetch(`${$apiBaseUrl}/api/v1/auth/authenticate`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ username, password })
			});

			if (response.ok) {
				const token = await response.text();
				localStorage.setItem('authToken', token);

				var newToken = localStorage.getItem('authToken');
				alert(`Login successful with token: ${newToken}`);
				// window.location.href = '/protected';
			} else {
				console.error(`Login failed with status: ${response.status}`);
				try {
					const errorResponse = await response.json();
					console.error('Error response:', errorResponse);
				} catch (error) {
					console.error('Error parsing response body:', error);
				}
				alert('Login failed');
			}
		} catch (error) {
			console.error('An unexpected error occurred:', error);
			alert('An unexpected error occurred');
		}
	}
</script>

<form on:submit|preventDefault={handleSubmit}>
	<input type="text" bind:value={username} placeholder="Username" />
	<input type="password" bind:value={password} placeholder="Password" />
	<button type="submit">Login</button>
</form>

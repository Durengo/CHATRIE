<script>
    // import { onMount } from 'svelte';
    // import cassandra from 'cassandra-driver';

    let message = '';

    async function connectToCassandra() {
		console.log('Connecting to Cassandra');
        try {
            const response = await fetch("/api/db");
            if (response.ok) {
                message = await response.text();
            } else {
                throw new Error('Failed to connect');
            }
        } catch (error) {
            console.error('Error:', error);
            message = 'Failed to connect to Cassandra.';
        }
    }
</script>

<svelte:head>
    <title>About</title>
    <meta name="description" content="About this app" />
</svelte:head>

<div class="text-column">
    <h1>About this app</h1>

    <!-- ... other content ... -->

    <button on:click={connectToCassandra}>Connect to Cassandra</button>
    <p>{message}</p>
</div>
